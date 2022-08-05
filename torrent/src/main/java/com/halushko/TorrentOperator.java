package com.halushko;

import com.halushko.rabKot.handlers.input.CliCommandExecutor;

public class TorrentOperator extends CliCommandExecutor {
    public static final String EXECUTE_TORRENT_COMMAND_QUEUE = System.getenv("EXECUTE_TORRENT_COMMAND_QUEUE");
    public static final String TELEGRAM_OUTPUT_TEXT = System.getenv("TELEGRAM_OUTPUT_TEXT_QUEUE");

    @Override
    protected String getQueue() {
        return EXECUTE_TORRENT_COMMAND_QUEUE;
    }

    @Override
    public String getParserId() {
        return "null";
    }
}
