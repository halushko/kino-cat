package com.halushko.kinocat.torrent.requests.common;

import com.halushko.kinocat.core.JsonConstants.SmartJsonKeys;
import com.halushko.kinocat.core.files.ResourceReader;
import com.halushko.kinocat.core.rabbit.SmartJson;
import com.halushko.kinocat.core.web.ApiResponce;
import com.halushko.kinocat.core.web.InputMessageHandlerApiRequest;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// https://github.com/transmission/transmission/blob/main/docs/rpc-spec.md
@Slf4j
public abstract class TransmissionWebApiExecutor extends InputMessageHandlerApiRequest {
    private final static Map<String, String> sessionIdValues = new HashMap<>();
    private final static Map<String, String> serverNumbers = new HashMap<>();

    protected final static String sessionIdKey = "X-Transmission-Session-Id";
    public static final String TRANSMISSION_IP = "[{\"ip\": \"192.168.50.132\",\"port\": \"9093\"}, {\"name\": \"hdd\",\"ip\": \"192.168.50.132\",\"port\": \"9092\"}]";//System.getenv("TORRENT_IP");

    public TransmissionWebApiExecutor() {
        super(TRANSMISSION_IP);
        int i = -1;
        for (String server : serverUrls.keySet()) {
            sessionIdValues.put(server, null);
            serverNumbers.put(String.valueOf(++i), server);
        }
    }

    @Override
    protected final String getDeliverCallbackPrivate(SmartJson message) {
        log.debug("[getDeliverCallbackPrivate] Message:\n{}", message.getRabbitMessageText());
        long chatId = message.getUserId();
        String selectedServer = message.getValue(SmartJsonKeys.SELECT_SERVER);
        List<String> serversToApply = new ArrayList<>();

        StringBuilder output = new StringBuilder();

        try {
            Integer.parseInt(selectedServer);
            if (serverNumbers.containsKey(selectedServer)) {
                serversToApply.add(selectedServer);
            } else {
                serversToApply.addAll(serverNumbers.keySet());
            }
        } catch (NumberFormatException e) {
            String errorText = String.format("Server number: '%s' is invalid", selectedServer);
            output.append(errorText);
            printResult(chatId, errorText);
        }

        for (val sn : serversToApply) {
            String serverUrl = serverNumbers.get(sn);
            if (sessionIdValues.get(sn) == null) {
                //new session
                log.debug("[getDeliverCallbackPrivate] Create a new session");

                val responce = send(serverUrl, "Content-Type", "application/json", "");
                sessionIdValues.put(serverUrl, responce.getHeader(sessionIdKey));
            }
            String requestBodyFormat = ResourceReader.readResourceContent(String.format("transmission_requests/%s", getRequest()));
            Object[] requestBodyFormatArguments = getRequestArguments(message.getSubMessage(SmartJsonKeys.COMMAND_ARGUMENTS));
            log.debug("[getDeliverCallbackPrivate] Request body format:\n{}\nRequest body format arguments:\n{}", requestBodyFormat, requestBodyFormatArguments);
            String requestBody = String.format(requestBodyFormat, requestBodyFormatArguments);
            log.debug("[getDeliverCallbackPrivate] Request body:\n{}", requestBody);

            ApiResponce responce = send(serverUrl, requestBody, serverUrl, "Content-Type", "application/json", sessionIdKey, sessionIdValues.get(sn));
            String responceBody = responce.getBody();
            if (responceBody.contains("409: Conflict")) {
                //expired session
                log.debug("[getDeliverCallbackPrivate] Recreate a session");
                TransmissionWebApiExecutor.sessionIdValues.put(serverUrl, responce.getHeader(sessionIdKey));
                responce = send(serverUrl, requestBody, "Content-Type", "application/json", sessionIdKey, sessionIdValues.get(sn));
                responceBody = responce.getBody();
            }

            log.debug("[getDeliverCallbackPrivate] Responce body:\n{}", responceBody);
            SmartJson json = new SmartJson(SmartJsonKeys.INPUT, message.getRabbitMessageText()).addValue(SmartJsonKeys.OUTPUT, responceBody);

            if (isResultValid(json)) {
                val result = parseResponce(json, sn.isEmpty() ? "" : "_" + sn);
                StringBuilder sb = null;
                for (int i = 1; i <= result.size(); i++) {
                    String answer = result.get(i - 1);
                    log.debug("[getDeliverCallbackPrivate] answer={}", answer);
                    if (i == 1 || i % 10 == 0) {
                        log.debug("[getDeliverCallbackPrivate] New message created");
                        if (sb != null) {
                            log.debug("[getDeliverCallbackPrivate] Print result:\n{}", sb);
                            output.append(printResult(chatId, sb.toString())).append(OUTPUT_SEPARATOR);
                        }
                        sb = new StringBuilder();
                        if (addDescription()) {
                            sb.append(serverUrl).append(": ").append(textOfMessageBegin()).append(i).append("-").append(result.size() < i + 10 ? result.size() : (i == 1 ? 9 : i + 9)).append("\n\n");
                        }
                    }
                    sb.append(answer).append(OUTPUT_SEPARATOR);
                    log.debug("[getDeliverCallbackPrivate] Message:\n{}", sb);
                }
                if (sb != null) {
                    log.debug("[getDeliverCallbackPrivate] Print result:\n{}", sb);
                    output.append(printResult(chatId, sb.toString()));
                } else {
                    log.debug("[getDeliverCallbackPrivate] Result is empty");
                    output.append(printResult(chatId, String.format("%s: Нажаль результат запиту порожній", serverUrl)));
                }
            } else {
                String errorText = String.format("Server: %s result of request is: %s", serverUrl, responceBody);
                output.append(errorText);
                printResult(chatId, errorText);
            }
        }
        return output.toString();
    }

    protected abstract List<String> parseResponce(SmartJson message, String serverNumber);

    protected boolean isResultValid(SmartJson result) {
        return "success".equalsIgnoreCase(result.getSubMessage(SmartJsonKeys.OUTPUT).getValue("result"));
    }

    protected String textOfMessageBegin() {
        return "";
    }

    protected boolean addDescription() {
        return textOfMessageBegin() != null && !textOfMessageBegin().isEmpty();
    }

    protected Object[] getRequestArguments(SmartJson message) {
        return message == null ? new Object[0] : message.convertToList().toArray();
    }

    @Override
    protected String getDefaultPort() {
        return "9091";
    }

    @Override
    protected String getDefaultSuffix() {
        return "transmission/rpc";
    }

}
