package com.halushko.kinocat.torrent.entities;

import com.halushko.kinocat.core.rabbit.SmartJson;
import lombok.Getter;
import lombok.val;

import java.util.Map;

@Getter
public class SubTorrentEntity {
    private final String parentId;
    private final String name;
    private final long length;
    private final long bytesCompleted;

    public SubTorrentEntity(Object obj, String parentId) {
        if (!(obj instanceof Map)) {
            throw new RuntimeException("[TorrentEntity] Can't parse torrent");
        }
        //noinspection unchecked
        val torrent = new SmartJson((Map<String, Object>) obj);
        this.parentId = parentId;
        this.name = torrent.getValue("name");

        String length = torrent.getValue("length");
        this.length = length.isEmpty() ? 0L : Long.parseLong(length);

        String bytesCompleted = torrent.getValue("bytesCompleted");
        this.bytesCompleted = length.isEmpty() ? 0L : Long.parseLong(bytesCompleted);
    }
}
