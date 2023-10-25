package com.halushko.kinocat.torrent.requests.concrete;

import com.halushko.kinocat.core.commands.Constants;
import com.halushko.kinocat.torrent.requests.common.VoidTorrentOperator;

public class PauseAll extends VoidTorrentOperator {
    @Override
    protected String getCommandName() {
        return "зупинки всіх торентів";
    }

    @Override
    protected String getRequest() {
        return "pause_all.json";
    }

    @Override
    protected String getQueue() {
        return Constants.Queues.Torrent.PAUSE_ALL;
    }
}
