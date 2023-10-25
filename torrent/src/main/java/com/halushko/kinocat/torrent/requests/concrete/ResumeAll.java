package com.halushko.kinocat.torrent.requests.concrete;

import com.halushko.kinocat.core.commands.Constants;
import com.halushko.kinocat.torrent.requests.common.VoidTorrentOperator;

public class ResumeAll extends VoidTorrentOperator {
    @Override
    protected String getCommandName() {
        return "відновлення роботи всіх торентів";
    }

    @Override
    protected String getRequest() {
        return "start_all.json";
    }

    @Override
    protected String getQueue() {
        return Constants.Queues.Torrent.RESUME_ALL;
    }
}
