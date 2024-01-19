package com.halushko.kinocat.torrent.requests.concrete;

import com.halushko.kinocat.core.Queues;
import com.halushko.kinocat.torrent.requests.common.VoidTorrentOperator;

public class PauseTorrent extends VoidTorrentOperator {
    @Override
    protected String getCommandNameForOutputText() {
        return "зупинки торента";
    }

    @Override
    protected String getRequest() {
        return "pause_torrent.json";
    }

    @Override
    protected String getQueue() {
        return Queues.Torrent.PAUSE_TORRENT;
    }
}
