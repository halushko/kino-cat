package com.halushko.kinocat.minidlna;

import com.halushko.kinocat.core.cli.Constants;
import com.halushko.kinocat.core.handlers.input.ExternalCliCommandExecutor;

@Deprecated
public class MinidlnaOperator extends ExternalCliCommandExecutor {
    @Override
    protected String getQueue() {
        return Constants.Queues.MediaServer.EXECUTE_MINIDLNA_COMMAND;
    }
}
