package com.halushko.kinocat.torrent.requests.common;

import com.halushko.kinocat.core.JsonConstants.SmartJsonKeys;
import com.halushko.kinocat.core.files.ResourceReader;
import com.halushko.kinocat.core.rabbit.SmartJson;
import com.halushko.kinocat.core.web.ApiResponce;
import com.halushko.kinocat.core.web.InputMessageHandlerApiRequest;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.*;

// https://github.com/transmission/transmission/blob/main/docs/rpc-spec.md
@Slf4j
public abstract class TransmissionWebApiExecutor extends InputMessageHandlerApiRequest {
    private final static Map<String, String> sessionIdValues = new HashMap<>();
    private final static Map<String, String> serverNames = new HashMap<>();

    protected final static String sessionIdKey = "X-Transmission-Session-Id";
    public static final String TRANSMISSION_IP = "[{\"ip\": \"192.168.50.132\",\"port\": \"9093\"}, {\"name\": \"hdd\",\"ip\": \"192.168.50.132\",\"port\": \"9092\"}]";//System.getenv("TORRENT_IP");

    public TransmissionWebApiExecutor() {
        super(TRANSMISSION_IP);
        int i = -1;
        for (val server : serverUrls.keySet()) {
            String serverNumber = String.valueOf(++i);
            sessionIdValues.put(serverNumber, null);
            serverNames.put(serverNumber, server);
        }
    }

    @Override
    protected final String getDeliverCallbackPrivate(SmartJson message) {
        log.debug("[getDeliverCallbackPrivate] Message:\n{}", message.getRabbitMessageText());
        long chatId = message.getUserId();
        List<String> serversToApply = new ArrayList<>();

        StringBuilder output = new StringBuilder();

        analyzeServer(message, serversToApply, output, chatId);

        for (val serverNumber : serversToApply) {
            String serverName = serverNames.get(serverNumber);
            
            generateSessionId(serverNumber, serverName);
            TransmissionResponce responce = getTransmissionResponce(message, serverNumber, serverName);

            if (isResultValid(responce.json())) {
                val result = parseResponce(responce.json(), serverNumber);
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
                            sb.append(serverName.isEmpty() ? "main" : serverName).append(": ")
                                    .append(textOfMessageBegin())
                                    .append(i).append("-").append(result.size() < i + 10 ? result.size() : (i == 1 ? 9 : i + 9))
                                    .append("\n\n");
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
                    output.append(printResult(chatId, String.format("%s: Нажаль результат запиту порожній", serverName)));
                }
            } else {
                String errorText = String.format("Server: %s result of request is: %s", serverName, responce.responceBody());
                output.append(errorText);
                printResult(chatId, errorText);
            }
        }
        return output.toString();
    }

    private TransmissionResponce getTransmissionResponce(SmartJson message, String serverNumber, String serverName) {
        String requestBody = generateRequestBody(message);

        ApiResponce responce = send(serverName, requestBody, serverName, "Content-Type", "application/json", sessionIdKey, sessionIdValues.get(serverNumber));
        String responceBody = responce.getBody();
        if (responceBody.contains("409: Conflict")) {
            //expired session
            log.debug("[getDeliverCallbackPrivate] Recreate a session");
            TransmissionWebApiExecutor.sessionIdValues.put(serverNumber, responce.getHeader(sessionIdKey));
            responce = send(serverName, requestBody, "Content-Type", "application/json", sessionIdKey, sessionIdValues.get(serverNumber));
            responceBody = responce.getBody();
        }

        log.debug("[getDeliverCallbackPrivate] Responce body:\n{}", responceBody);
        SmartJson json = new SmartJson(SmartJsonKeys.INPUT, message.getRabbitMessageText()).addValue(SmartJsonKeys.OUTPUT, responceBody);
        return new TransmissionResponce(responceBody, json);
    }

    private record TransmissionResponce(String responceBody, SmartJson json) {
    }

    private String generateRequestBody(SmartJson message) {
        String requestBodyFormat = ResourceReader.readResourceContent(String.format("transmission_requests/%s", getRequest()));
        Object[] requestBodyFormatArguments = getRequestArguments(message.getSubMessage(SmartJsonKeys.COMMAND_ARGUMENTS));
        if(requestBodyFormatArguments.length > 1) {
            requestBodyFormatArguments = Arrays.copyOfRange(requestBodyFormatArguments, 1, requestBodyFormatArguments.length);
        }
        log.debug("[getDeliverCallbackPrivate] Request body format:\n{}\nRequest body format arguments:\n{}", requestBodyFormat, requestBodyFormatArguments);
        String requestBody = String.format(requestBodyFormat, requestBodyFormatArguments);
        log.debug("[getDeliverCallbackPrivate] Request body:\n{}", requestBody);
        return requestBody;
    }

    private void generateSessionId(String serverNumber, String serverName) {
        if (sessionIdValues.get(serverNumber) == null) {
            //new session
            log.debug("[getDeliverCallbackPrivate] Create a new session");

            val responce = send(serverName, "Content-Type", "application/json", "");
            sessionIdValues.put(serverNumber, responce.getHeader(sessionIdKey));
        }
    }

    private void analyzeServer(SmartJson message, List<String> serversToApply, StringBuilder output, long chatId) {
        String selectedServer = message.getValue(SmartJsonKeys.SELECT_SERVER);
        if(!selectedServer.isEmpty()) {
            try {
                Integer.parseInt(selectedServer);
                if (serverNames.containsKey(selectedServer)) {
                    serversToApply.add(selectedServer);
                } else {
                    String errorText = String.format("Server number: '%s' is invalid", selectedServer);
                    output.append(errorText);
                    printResult(chatId, errorText);
                }
            } catch (NumberFormatException e) {
                String errorText = String.format("Server number: '%s' is invalid", selectedServer);
                output.append(errorText);
                printResult(chatId, errorText);
            }
        } else {
            serversToApply.addAll(serverNames.keySet());
        }
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
