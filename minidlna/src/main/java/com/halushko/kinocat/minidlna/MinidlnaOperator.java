package com.halushko.kinocat.minidlna;

import com.halushko.kinocat.middleware.handlers.input.CliCommandExecutor;

public class MinidlnaOperator extends CliCommandExecutor {
    public static final String EXECUTE_MINIDLNA_COMMAND_QUEUE = System.getenv("EXECUTE_MINIDLNA_COMMAND_QUEUE");

    @Override
    protected String getQueue() {
        return EXECUTE_MINIDLNA_COMMAND_QUEUE;
    }
}
