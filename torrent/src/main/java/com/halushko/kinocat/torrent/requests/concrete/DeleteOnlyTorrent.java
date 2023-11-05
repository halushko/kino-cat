package com.halushko.kinocat.torrent.requests.concrete;

import com.halushko.kinocat.core.commands.Constants;
import com.halushko.kinocat.torrent.requests.common.VoidTorrentOperator;

public class DeleteOnlyTorrent extends VoidTorrentOperator {
    @Override
    protected String getCommandNameForOutputText() {
        return "видалення торента";
    }

    @Override
    protected String getRequest() {
        return "remove_only_torrent.json";
    }

    @Override
    protected String getQueue() {
        return Constants.Queues.Torrent.DELETE_ONLY_TORRENT;
    }
}
