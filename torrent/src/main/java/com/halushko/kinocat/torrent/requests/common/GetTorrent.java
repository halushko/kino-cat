package com.halushko.kinocat.torrent.requests.common;

import com.halushko.kinocat.core.rabbit.SmartJson;
import com.halushko.kinocat.torrent.entities.TorrentEntity;

import java.util.Comparator;
import java.util.List;

public abstract class GetTorrent extends TransmissionWebApiExecutor {
    @Override
    protected final List<String> executeRequest(SmartJson json) {
        return json.getSubMessage("OUTPUT")
                        .getSubMessage("arguments")
                        .getSubMessage("torrents")
                        .convertToList()
                        .stream()
                        .map(TorrentEntity::new)
                        .sorted(Comparator.comparing(TorrentEntity::getName))
                        .map(this::generateAnswer)
                        .toList();
    }

    protected abstract String generateAnswer(TorrentEntity torrent);
}
