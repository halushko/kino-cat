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
        String requestBodyFormat = ResourceReader.readResourceContent(String.format("transmission_requests/%s", message.getValue("SCRIPT")));
        String requestBody = String.format(requestBodyFormat, (Object[]) message.getValue("ARG").split(" "));
        log.debug("[executeRequest] Request body:\n{}", requestBody);

        ApiResponce responce = send(requestBody, "Content-Type", "application/json", sessionIdKey, sessionIdValue);
        String responceBody = responce.getBody();
        if(responceBody.contains("409: Conflict")){
            //expired session
            log.debug("[executeRequest] Recreate a session");
            TransmissionWebApiExecutor.sessionIdValue = responce.getHeader(sessionIdKey);
            responce = send(requestBody, "Content-Type", "application/json", sessionIdKey, sessionIdValue);
            responceBody = responce.getBody();
        }

        log.debug("[executeRequest] Responce body:\n{}", responceBody);
        val json = new SmartJson("INPUT", message.getRabbitMessageText()).addValue("OUTPUT", responceBody);

        String requestResult = json.getSubMessage("OUTPUT").getValue("result");

        String output;
        if ("success".equalsIgnoreCase(requestResult)) {
            output = executeRequest(json);
        } else {
            output = String.format("result of request is: %s", responceBody);
        }

        RabbitUtils.postMessage(chatId, output, Constants.Queues.Telegram.TELEGRAM_OUTPUT_TEXT);

    }

    protected abstract String executeRequest(SmartJson message);
}
