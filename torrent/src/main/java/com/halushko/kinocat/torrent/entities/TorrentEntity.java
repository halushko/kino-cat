package com.halushko.kinocat.torrent.entities;

import com.halushko.kinocat.core.rabbit.SmartJson;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Slf4j
public class TorrentEntity {
    private final String id;
    private final String name;
    private final double percentDone;
    private final int status;
    private final long totalSize;
    private final long eta;
    private final List<SubTorrentEntity> files = new ArrayList<>();

    public TorrentEntity(Object obj) {
        if (!(obj instanceof Map)) {
            throw new RuntimeException("[TorrentEntity] Can't parse torrent");
        }
        //noinspection unchecked
        val torrent = new SmartJson((Map<String, Object>) obj);
        this.id = torrent.getValue("id");
        this.name = torrent.getValue("name");
        String status = torrent.getValue("status");
        this.status = status.isEmpty() ? -1 : Integer.parseInt(status);

        String percentDone = torrent.getValue("percentDone");
        this.percentDone = percentDone.isEmpty() ? 0.0 : Double.parseDouble(percentDone);
        String totalSize = torrent.getValue("totalSize");
        this.totalSize = percentDone.isEmpty() ? 0L : Long.parseLong(totalSize);

        String eta = torrent.getValue("eta");
        this.eta = eta.isEmpty() ? -1L : Long.parseLong(totalSize);

        fillSubTorrents(torrent.getSubMessage("files"));
    }

    private void fillSubTorrents(SmartJson json) {
        json.convertToList().forEach(file -> files.add(new SubTorrentEntity(file, id)));
    }
}
