package com.halushko.kinocat.file;

import java.util.Map;
import java.util.stream.Collectors;

public interface Constants {
    String PATH_TO_UNAPPROVED_FOLDER = "/tmp/unapproved";
    String PATH_TO_DESTINATION_FOLDER = "/home/torrent_files";
    String EMPTY_SERVICE_DEFAULT_NAME = "main";
    Map<String, String> FOLDERS = new FoldersProcessor(System.getenv("TORRENT_IP"))
            .values.keySet().stream()
            .collect(Collectors.toMap(key -> key, key -> String.format(PATH_TO_DESTINATION_FOLDER + "%s", !key.isEmpty() ? "_" + key : "")));
    String NAME_LINE = "^\\s+Name:\\s+.*";
    String SIZE_LINE = "^\\s+Total Size:\\s+.*";

}
