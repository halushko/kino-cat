package com.halushko.kinocat.torrent.requests.concrete;

import com.halushko.kinocat.core.commands.Constants;
import com.halushko.kinocat.torrent.requests.common.ExecuteForAllTorrents;

public class ResumeAll extends ExecuteForAllTorrents {
    @Override
    protected String getQueue() {
        return Constants.Queues.Torrent.RESUME_ALL;
    }

    @Override
    protected String getQueueForPostAction() {
        return Constants.Queues.Torrent.RESUME_TORRENT;
    }
}
