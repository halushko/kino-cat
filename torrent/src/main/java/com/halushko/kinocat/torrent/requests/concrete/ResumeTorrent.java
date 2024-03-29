package com.halushko.kinocat.torrent.requests.concrete;

import com.halushko.kinocat.core.Queues;
import com.halushko.kinocat.torrent.requests.common.VoidTorrentOperator;

public class ResumeTorrent extends VoidTorrentOperator {
    @Override
    protected String getCommandNameForOutputText() {
        return "відновлення завантаження торента";
    }

    @Override
    protected String getRequest() {
        return "resume_torrent.json";
    }

    @Override
    protected String getQueue() {
        return Queues.Torrent.RESUME_TORRENT;
    }
}
