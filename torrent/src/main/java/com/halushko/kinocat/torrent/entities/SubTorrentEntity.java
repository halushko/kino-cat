package com.halushko.kinocat.torrent.entities;

import com.halushko.kinocat.core.rabbit.SmartJson;
import lombok.Getter;
import lombok.val;

import java.util.*;

@Getter
public class SubTorrentEntity {
    private final String parentId;
    private final String name;
    private final long length;
    private final long bytesCompleted;
    private final List<String> folders = new ArrayList<>();

    SubTorrentEntity(Object obj, String parentId) {
        if (!(obj instanceof Map)) {
            throw new RuntimeException("[SubTorrentEntity] Can't parse torrent");
        }
        //noinspection unchecked
        val torrent = new SmartJson((Map<String, Object>) obj);

        this.parentId = parentId;
        String length = torrent.getValue("length");
        this.length = length.isEmpty() ? 0L : Long.parseLong(length);
        String bytesCompleted = torrent.getValue("bytesCompleted");
        this.bytesCompleted = bytesCompleted.isEmpty() ? 0L : Long.parseLong(bytesCompleted);

        String fullName = torrent.getValue("name");

        String[] parts = fullName.split("/");
        this.name = parts[parts.length - 1];
        if (parts.length > 1) {
            folders.addAll(Arrays.asList(parts).subList(0, parts.length - 2));
        }
    }
}
