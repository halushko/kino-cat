package com.halushko.kinocat.torrent.requests.common;

import com.halushko.kinocat.core.files.ResourceReader;
import com.halushko.kinocat.core.rabbit.SmartJson;
import com.halushko.kinocat.core.web.ApiResponce;
import com.halushko.kinocat.core.web.InputMessageHandlerApiRequest;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.List;

// https://github.com/transmission/transmission/blob/main/docs/rpc-spec.md
@Slf4j
public abstract class TransmissionWebApiExecutor extends InputMessageHandlerApiRequest {
    private static String sessionIdValue;
    protected final static String sessionIdKey = "X-Transmission-Session-Id";

    public TransmissionWebApiExecutor() {
        super("http", "10.10.255.253", 9091, "transmission/rpc");
    }

    @Override
    protected final String getDeliverCallbackPrivate(SmartJson message) {
        log.debug("[getDeliverCallbackPrivate] Message:\n{}", message.getRabbitMessageText());
        long chatId = message.getUserId();
        if (sessionIdValue == null) {
            //new session
            log.debug("[getDeliverCallbackPrivate] Create a new session");

            val responce = send("", "Content-Type", "application/json");
            TransmissionWebApiExecutor.sessionIdValue = responce.getHeader(sessionIdKey);
        }
        String requestBodyFormat = ResourceReader.readResourceContent(String.format("transmission_requests/%s", getRequest()));
        Object[] requestBodyFormatArguments = getRequestArguments(message.getSubMessage(SmartJson.KEYS.COMMAND_ARGUMENTS));
        log.debug("[getDeliverCallbackPrivate] Request body format:\n{}\nRequest body format arguments:\n{}", requestBodyFormat, requestBodyFormatArguments);
        String requestBody = String.format(requestBodyFormat, requestBodyFormatArguments);
        log.debug("[getDeliverCallbackPrivate] Request body:\n{}", requestBody);

        ApiResponce responce = send(requestBody, "Content-Type", "application/json", sessionIdKey, sessionIdValue);
        String responceBody = responce.getBody();
        if (responceBody.contains("409: Conflict")) {
            //expired session
            log.debug("[getDeliverCallbackPrivate] Recreate a session");
            TransmissionWebApiExecutor.sessionIdValue = responce.getHeader(sessionIdKey);
            responce = send(requestBody, "Content-Type", "application/json", sessionIdKey, sessionIdValue);
            responceBody = responce.getBody();
        }

        log.debug("[getDeliverCallbackPrivate] Responce body:\n{}", responceBody);
        SmartJson json = new SmartJson(SmartJson.KEYS.INPUT, message.getRabbitMessageText()).addValue(SmartJson.KEYS.OUTPUT, responceBody);
        StringBuilder output = new StringBuilder();

        if (isResultValid(json)) {
            val result = parceResponce(json);
            int i = 999;
            int j = -1;
            StringBuilder sb = null;
            for (String answer : result) {
                log.debug("[getDeliverCallbackPrivate] answer={}", answer);
                if (++i > 10) {
                    log.debug("[getDeliverCallbackPrivate] New message created");
                    i = 1;
                    j++;
                    if (sb != null) {
                        log.debug("[getDeliverCallbackPrivate] Print result:\n{}", sb);
                        output.append(printResult(chatId, sb.toString())).append(OUTPUT_SEPARATOR);
                    }
                    sb = new StringBuilder();
                    if (addDescription()) {
                        sb.append(textOfMessageBegin()).append(j * 10).append("-").append(result.size() <= (j + 1) * 10 ? result.size() : j * 10 + 9).append("\n\n");
                    }
                }
                sb.append(answer).append(OUTPUT_SEPARATOR);
                log.debug("[getDeliverCallbackPrivate] Message:\n{}", sb);
            }
            if (sb != null) {
                log.debug("[getDeliverCallbackPrivate] Print result:\n{}", sb);
                output.append(printResult(chatId, sb.toString()));
            }
        } else {
            String errorText = String.format("result of request is: %s", responceBody);
            output.append(errorText);
            printResult(chatId, errorText);
        }
        return output.toString();
    }

    protected abstract List<String> parceResponce(SmartJson message);

    protected boolean isResultValid(SmartJson result) {
        return "success".equalsIgnoreCase(result.getSubMessage(SmartJson.KEYS.OUTPUT).getValue("result"));
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
}
