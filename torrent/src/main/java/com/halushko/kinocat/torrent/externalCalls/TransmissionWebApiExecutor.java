package com.halushko.kinocat.torrent.externalCalls;

import com.halushko.kinocat.core.cli.Constants;
import com.halushko.kinocat.core.files.ResourceReader;
import com.halushko.kinocat.core.rabbit.RabbitUtils;
import com.halushko.kinocat.core.rabbit.SmartJson;
import com.halushko.kinocat.core.web.ApiResponce;
import com.halushko.kinocat.core.web.InputMessageHandlerApiRequest;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

// https://github.com/transmission/transmission/blob/main/docs/rpc-spec.md
@Slf4j
public abstract class TransmissionWebApiExecutor extends InputMessageHandlerApiRequest {
    protected String sessionIdValue;
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
            val responce = send("", "Content-Type", "application/json");
            this.sessionIdValue = responce.getHeader(sessionIdKey);
        }
        String arguments = message.getValue("ARG");
        String requestBodyFormat = ResourceReader.readResourceContent(String.format("transmission_requests/%s", message.getValue("SCRIPT")));
        String requestBody = String.format(requestBodyFormat, (Object[]) arguments.split(" "));
        log.debug("[executeRequest] Request body:\n{}", requestBody);

        ApiResponce responce = send(requestBody, "Content-Type", "application/json", sessionIdKey, sessionIdValue);
        if(responce.getHeader("Content-Type").contains("409: Conflict")){
            //expired session
            this.sessionIdValue = responce.getHeader(sessionIdKey);
            responce = send(requestBody, "Content-Type", "application/json", sessionIdKey, sessionIdValue);
        }


        String bodyJson = responce.getBody();
        log.debug("[executeRequest] Responce body:\n{}", bodyJson);
        val json = new SmartJson(bodyJson);

        String requestResult = json.getValue("result");

        String output;
        if ("success".equalsIgnoreCase(requestResult)) {
            output = executeRequest(json);
        } else {
            output = String.format("result of request is: %s", requestResult);
        }

        RabbitUtils.postMessage(chatId, output, Constants.Queues.Telegram.TELEGRAM_OUTPUT_TEXT);

    }

    protected abstract String executeRequest(SmartJson message);
}
