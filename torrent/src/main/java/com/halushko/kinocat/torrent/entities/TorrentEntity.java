package com.halushko.kinocat.torrent.entities;

import com.halushko.kinocat.core.rabbit.SmartJson;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

@Getter
@Slf4j
public class TorrentEntity {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private final String id;
    private final String name;
    private final int status;

    private final long totalSize;
    private final long eta;
    private final long uploadedEver;
    private final double percentDone;

    private final String lastActivityDate;
    private final String dateCreated;
    private final String addedDate;

    private final String comment;
    private final String errorString;

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
        this.comment = torrent.getValue("comment");
        this.errorString = torrent.getValue("errorString");
        this.status = (int)getLong(torrent.getValue("status"), -1);

        String percentDone = torrent.getValue("percentDone");
        this.percentDone = percentDone.isEmpty() ? 0.0 : Double.parseDouble(percentDone);

        this.totalSize = getLong(torrent.getValue("totalSize"));
        this.eta = getLong(torrent.getValue("eta"), -1L);
        this.uploadedEver = getLong(torrent.getValue("uploadedEver"));

        val files = torrent.getSubMessage("files").convertToList();
        files.forEach(file -> this.files.add(new SubTorrentEntity(file, id)));

        this.lastActivityDate = getDate(torrent.getValue("activityDate"));
        this.dateCreated = getDate(torrent.getValue("dateCreated")); //TODO check
        this.addedDate = getDate(torrent.getValue("addedDate"));
    }

    private static String getDate(String strValue) {
        if(strValue.isEmpty()) return "";
        long timestamp;
        try {
            timestamp = Long.parseLong(strValue);
        } catch (NumberFormatException e) {
            timestamp = 0;
        }
        Instant instant = Instant.ofEpochSecond(timestamp);
        ZonedDateTime date = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());
        return date.format(FORMATTER);
    }
    private static long getLong(String strValue){
        return getLong(strValue, 0);
    }
    private static long getLong(String strValue, long defaultValue) {
        try {
            return strValue.isEmpty() ? defaultValue : Long.parseLong(strValue);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
