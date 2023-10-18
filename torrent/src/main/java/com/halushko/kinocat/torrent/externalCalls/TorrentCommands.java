package com.halushko.kinocat.torrent.externalCalls;

import com.halushko.kinocat.core.cli.Constants;
import com.halushko.kinocat.core.rabbit.SmartJson;
import com.halushko.kinocat.torrent.entities.CommonTorrentEntity;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.Map;

@Slf4j
public class TorrentCommands extends TransmissionWebApiExecutor {
    public TorrentCommands() {
        super();
    }

    @Override
    protected String executeRequest(SmartJson json) {
        StringBuilder sb = new StringBuilder();
        json.getSubMessage("arguments")
                .getSubMessage("torrents")
                .convertToList()
                .forEach(torrentMap -> {
                    if (torrentMap instanceof Map) {
                        //noinspection unchecked
                        val torrent = new CommonTorrentEntity((Map<String, Object>) torrentMap);
                        sb.append(torrent.generateTorrentCommands()).append("\n");
                    } else {
                        throw new RuntimeException("Can't generate torrent commands for torrent");
                    }
                });
        return sb.toString();
    }

    @Override
    protected String getQueue() {
        return Constants.Queues.Torrent.EXECUTE_TORRENT_COMMAND_COMMANDS;
    }
}
