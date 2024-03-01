package com.halushko.kinocat.torrent.requests.concrete;

import com.halushko.kinocat.core.Queues;
import com.halushko.kinocat.torrent.requests.common.ExecuteForAllTorrents;

public class ResumeAll extends ExecuteForAllTorrents {
    @Override
    protected String getQueue() {
        return Queues.Torrent.RESUME_ALL;
    }

    @Override
    protected String getQueueForPostAction() {
        return Queues.Torrent.RESUME_TORRENT;
    }
}
