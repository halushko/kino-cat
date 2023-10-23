package com.halushko.kinocat.torrent.externalCalls;

import com.halushko.kinocat.core.cli.Constants;
import com.halushko.kinocat.torrent.entities.TorrentEntity;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TorrentCommands extends GetTorrent {
    public TorrentCommands() {
        super();
    }

    @Override
    protected String generateAnswer(TorrentEntity torrent){
        return String.format("%s\n%s%s\n%s%s\n%s%s\n%s%s", torrent.getName(),
                Constants.Commands.Torrent.PAUSE, torrent.getId(),
                Constants.Commands.Torrent.RESUME, torrent.getId(),
                Constants.Commands.Torrent.TORRENT_INFO, torrent.getId(),
                Constants.Commands.Text.REMOVE_COMMAND, torrent.getId());
    }

    @Override
    protected String getQueue() {
        return Constants.Queues.Torrent.EXECUTE_TORRENT_COMMAND_COMMANDS;
    }
}
