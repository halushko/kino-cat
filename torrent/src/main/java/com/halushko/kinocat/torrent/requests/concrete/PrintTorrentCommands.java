package com.halushko.kinocat.torrent.requests.concrete;

import com.halushko.kinocat.core.Commands;
import com.halushko.kinocat.core.Queues;
import com.halushko.kinocat.torrent.entities.TorrentEntity;
import com.halushko.kinocat.torrent.requests.common.GetTorrent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PrintTorrentCommands extends GetTorrent {
    public PrintTorrentCommands() {
        super();
    }

    @Override
    protected String generateAnswer(TorrentEntity torrent, String serverNumber, String serverVsTorrentSeparator) {
        return String.format("%s\n%s\n%s\n%s\n%s"
                , torrent.getName()
                , String.format("%s%s%s%s", Commands.Torrent.PAUSE, serverNumber, serverVsTorrentSeparator, torrent.getId())
                , String.format("%s%s%s%s", Commands.Torrent.RESUME, serverNumber, serverVsTorrentSeparator, torrent.getId())
                , String.format("%s%s%s%s", Commands.Torrent.TORRENT_INFO, serverNumber, serverVsTorrentSeparator, torrent.getId())
                , String.format("%s%s%s%s", Commands.Torrent.REMOVE_COMMAND, serverNumber, serverVsTorrentSeparator, torrent.getId())
        );
    }

    @Override
    protected String getQueue() {
        return Queues.Torrent.TORRENT_COMMANDS;
    }

    @Override
    protected String getRequest() {
        return "get_torrents_names.json";
    }
}
