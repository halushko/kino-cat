package com.halushko.kinocat.torrent.entities;

import com.halushko.kinocat.core.rabbit.SmartJson;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.List;
import java.util.Map;
import java.util.TreeSet;

@Getter
@Slf4j
public class TorrentEntity {
    private final String id;
    private final String name;
    private final double percentDone;
    private final int status;
    private final long totalSize;
    private final long eta;
    private final TreeSet<SubTorrentEntity> files = new TreeSet<>((str1, str2) -> {
        List<String> folders1 = str1.getFolders();
        List<String> folders2 = str2.getFolders();

        for (int i = 0; i < Math.min(folders1.size(), folders2.size()); i++) {
            int result = folders1.get(i).compareTo(folders2.get(i));
            if (result != 0) {
                return result;
            }
        }

        if(folders1.size() != folders2.size()) {
            return Integer.compare(folders1.size(), folders2.size());
        }

        return str1.getName().compareTo(str2.getName());
    });

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

        val files = torrent.getSubMessage("files").convertToList();
        files.forEach(file -> this.files.add(new SubTorrentEntity(file, id)));
    }
}
