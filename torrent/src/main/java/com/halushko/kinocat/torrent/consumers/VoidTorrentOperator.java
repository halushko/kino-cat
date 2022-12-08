package com.halushko.kinocat.torrent.consumers;

import com.halushko.kinocat.middleware.handlers.input.ExternalCliCommandExecutor;

public class VoidTorrentOperator extends ExternalCliCommandExecutor {
    public static final String EXECUTE_TORRENT_COMMAND_QUEUE = System.getenv("EXECUTE_TORRENT_COMMAND_QUEUE");
    @Override
    protected String getQueue() {
        return EXECUTE_TORRENT_COMMAND_QUEUE;
    }
}
