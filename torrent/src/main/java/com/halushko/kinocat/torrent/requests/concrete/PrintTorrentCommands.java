package com.halushko.kinocat.torrent.requests.concrete;

import com.halushko.kinocat.core.commands.Constants;
import com.halushko.kinocat.torrent.entities.TorrentEntity;
import com.halushko.kinocat.torrent.requests.common.GetTorrent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PrintTorrentCommands extends GetTorrent {
    public PrintTorrentCommands() {
        super();
    }

    @Override
    protected String generateAnswer(TorrentEntity torrent){
        return String.format("%s\n%s%s\n%s%s\n%s%s\n%s%s", torrent.getName(),
                Constants.Commands.Torrent.PAUSE, torrent.getId(),
                Constants.Commands.Torrent.RESUME, torrent.getId(),
                Constants.Commands.Torrent.TORRENT_INFO, torrent.getId(),
                Constants.Commands.Torrent.REMOVE_COMMAND, torrent.getId());
    }

    @Override
    protected String getQueue() {
        return Constants.Queues.Torrent.TORRENT_COMMANDS;
    }

    @Override
    protected String getRequest() {
        return "get_torrents_names.json";
    }
}
