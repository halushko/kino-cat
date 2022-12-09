package com.halushko.kinocat.torrent.externalCalls;

import com.halushko.kinocat.middleware.cli.Constants;
import com.halushko.kinocat.middleware.handlers.input.ExternalCliCommandExecutor;

public class VoidTorrentOperator extends ExternalCliCommandExecutor {
    @Override
    protected String getQueue() {
        return Constants.Queues.Torrent.EXECUTE_VOID_TORRENT_COMMAND;
    }
}
