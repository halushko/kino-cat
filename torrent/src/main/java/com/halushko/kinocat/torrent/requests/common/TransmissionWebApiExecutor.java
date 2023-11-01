package com.halushko.kinocat.torrent.requests.common;

import com.halushko.kinocat.core.commands.Constants;
import com.halushko.kinocat.core.files.ResourceReader;
import com.halushko.kinocat.core.rabbit.RabbitUtils;
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
    protected final void getDeliverCallbackPrivate(SmartJson message) {
        log.debug("[executeRequest] Message:\n{}", message.getRabbitMessageText());
        long chatId = message.getUserId();
        if (sessionIdValue == null) {
            //new session
            log.debug("[executeRequest] Create a new session");

            val responce = send("", "Content-Type", "application/json");
            TransmissionWebApiExecutor.sessionIdValue = responce.getHeader(sessionIdKey);
        }
        String requestBodyFormat = ResourceReader.readResourceContent(String.format("transmission_requests/%s", getRequest()));
        String requestBody = String.format(requestBodyFormat, message.getSubMessage(SmartJson.KEYS.COMMAND_ARGUMENTS).convertToList().toArray());
        log.debug("[executeRequest] Request body:\n{}", requestBody);

        ApiResponce responce = send(requestBody, "Content-Type", "application/json", sessionIdKey, sessionIdValue);
        String responceBody = responce.getBody();
        if (responceBody.contains("409: Conflict")) {
            //expired session
            log.debug("[executeRequest] Recreate a session");
            TransmissionWebApiExecutor.sessionIdValue = responce.getHeader(sessionIdKey);
            responce = send(requestBody, "Content-Type", "application/json", sessionIdKey, sessionIdValue);
            responceBody = responce.getBody();
        }

        log.debug("[executeRequest] Responce body:\n{}", responceBody);
        SmartJson json = new SmartJson(SmartJson.KEYS.INPUT, message.getRabbitMessageText()).addValue(SmartJson.KEYS.OUTPUT, responceBody);

        if (isResultValid(json)) {
            val result = executeRequest(json);
            int i = 999;
            int j = -1;
            boolean flag = false;
            StringBuilder sb = null;
            for (String answer : result) {
                if (++i > 10) {
                    log.debug("[executeRequest] New message created:\n{}", sb);
                    i = 1;
                    j++;
                    if(sb != null) {
                        RabbitUtils.postMessage(chatId, sb.toString(), Constants.Queues.Telegram.TELEGRAM_OUTPUT_TEXT);
                        flag = false;
                    }
                    sb = new StringBuilder();
                    if(addDescription()) {
                        sb.append(partitionDescription()).append(j * 10).append("-").append(result.size() <= (j+1) * 10 ? result.size() : j * 10 + 9).append("\n\n");
                    }
                } else {
                    flag = true;
                }
                sb.append(answer).append("\n");
                log.debug("[executeRequest] Message:\n{}", sb);
            }
            if(flag) {
                RabbitUtils.postMessage(chatId, sb.toString(), Constants.Queues.Telegram.TELEGRAM_OUTPUT_TEXT);
            }
        } else {
            RabbitUtils.postMessage(chatId, String.format("result of request is: %s", responceBody), Constants.Queues.Telegram.TELEGRAM_OUTPUT_TEXT);
        }
    }

    protected abstract List<String> executeRequest(SmartJson message);

    protected boolean isResultValid(SmartJson result) {
        return "success".equalsIgnoreCase(result.getSubMessage(SmartJson.KEYS.OUTPUT).getValue("result"));
    }

    protected String partitionDescription() {
        return "";
    }

    protected boolean addDescription() {
        return partitionDescription() != null && !partitionDescription().isEmpty();
    }
}
