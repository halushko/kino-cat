package com.halushko.kinocat.torrent;

import com.halushko.kinocat.middleware.handlers.input.CliCommandExecutor;

public class TorrentOperator extends CliCommandExecutor {
    public static final String EXECUTE_TORRENT_COMMAND_QUEUE = System.getenv("EXECUTE_TORRENT_COMMAND_QUEUE");

    @Override
    protected String getQueue() {
        return EXECUTE_TORRENT_COMMAND_QUEUE;
    }
}
