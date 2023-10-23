package com.halushko.kinocat.torrent.externalCalls;

import com.halushko.kinocat.core.rabbit.SmartJson;
import com.halushko.kinocat.torrent.entities.TorrentEntity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public abstract class GetTorrent extends TransmissionWebApiExecutor {
    @Override
    protected final String executeRequest(SmartJson json) {
        List<TorrentEntity> torrents = new ArrayList<>();
        json.getSubMessage("OUTPUT")
                .getSubMessage("arguments")
                .getSubMessage("torrents")
                .convertToList()
                .forEach(torrentMap -> torrents.add(new TorrentEntity(torrentMap)));
        StringBuilder sb = new StringBuilder();
        torrents.sort(Comparator.comparing(TorrentEntity::getName));
        torrents.forEach(torrent -> sb.append(generateAnswer(torrent)).append("\n"));
        return sb.toString();
    }

    protected abstract String generateAnswer(TorrentEntity torrent);
}
