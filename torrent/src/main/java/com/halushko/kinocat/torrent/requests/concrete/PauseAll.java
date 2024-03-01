package com.halushko.kinocat.torrent.requests.concrete;

import com.halushko.kinocat.core.Queues;
import com.halushko.kinocat.torrent.requests.common.ExecuteForAllTorrents;

public class PauseAll extends ExecuteForAllTorrents {
    @Override
    protected String getQueue() {
        return Queues.Torrent.PAUSE_ALL;
    }

    @Override
    protected String getQueueForPostAction() {
        return Queues.Torrent.PAUSE_TORRENT;
    }
}
