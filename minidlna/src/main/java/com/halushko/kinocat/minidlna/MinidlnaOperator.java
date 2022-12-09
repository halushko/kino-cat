package com.halushko.kinocat.minidlna;

import com.halushko.kinocat.middleware.cli.Constants;
import com.halushko.kinocat.middleware.handlers.input.ExternalCliCommandExecutor;

public class MinidlnaOperator extends ExternalCliCommandExecutor {
    @Override
    protected String getQueue() {
        return Constants.Queues.MediaServer.EXECUTE_MINIDLNA_COMMAND;
    }
}
