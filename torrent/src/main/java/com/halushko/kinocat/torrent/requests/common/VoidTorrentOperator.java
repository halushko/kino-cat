package com.halushko.kinocat.torrent.requests.common;

import com.halushko.kinocat.core.rabbit.SmartJson;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public abstract class VoidTorrentOperator extends TransmissionWebApiExecutor {
    @Override
    protected List<String> parseResponce(SmartJson message) {
        List<String> result = new ArrayList<>();
        result.add(String.format("Операція %s виконана без помилок", getCommandNameForOutputText()));
        return result;
    }

    protected abstract String getCommandNameForOutputText();
}
