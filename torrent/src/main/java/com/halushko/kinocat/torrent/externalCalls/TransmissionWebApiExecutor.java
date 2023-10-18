package com.halushko.kinocat.torrent.externalCalls;

import com.halushko.kinocat.core.files.ResourceReader;
import com.halushko.kinocat.core.rabbit.SmartJson;
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

        if(sessionIdValue == null) {
            val responce = send("", "Content-Type", "application/json");
            String sessionIdKey = "X-Transmission-Session-Id";
            this.sessionIdValue = responce.getHeader(sessionIdKey);
        }
        executeRequest(message);
    }

    protected abstract void executeRequest(SmartJson message);
}
