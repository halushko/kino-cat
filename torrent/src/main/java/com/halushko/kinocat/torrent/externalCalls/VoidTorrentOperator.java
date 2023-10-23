package com.halushko.kinocat.torrent.externalCalls;

import com.halushko.kinocat.core.cli.Constants;
import com.halushko.kinocat.core.rabbit.SmartJson;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VoidTorrentOperator extends TransmissionWebApiExecutor {
    @Override
    protected String getQueue() {
        return Constants.Queues.Torrent.EXECUTE_VOID_TORRENT_COMMAND;
    }

    @Override
    protected String executeRequest(SmartJson message) {
        log.debug("[executeRequest] input={}", message.getRabbitMessageText());
        return String.format("Команда [%s] виконана без помилок", message.getValue("DESCRIPTION"));
    }
}
