package com.halushko.kinocat.torrent.requests.common;

import com.halushko.kinocat.core.rabbit.SmartJson;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class VoidTorrentOperator extends TransmissionWebApiExecutor {
    @Override
    protected String executeRequest(SmartJson message) {
        return String.format("Операція %s виконана без помилок", getCommandName());
    }

    protected abstract String getCommandName();
}
