package com.halushko;

import com.halushko.rabKot.handlers.input.InputMessageHandler;
import com.halushko.rabKot.rabbit.RabbitMessage;
import com.halushko.rabKot.cli.ExecuteBash;
import com.halushko.rabKot.rabbit.RabbitUtils;

import java.util.List;

public class TorrentOperator extends InputMessageHandler {
    public static final String EXECUTE_TORRENT_COMMAND;
    static {
        String str = "EXECUTE_TORRENT_COMMAND";
        try {
            str = System.getenv("EXECUTE_TORRENT_COMMAND");
        } catch (Exception ignore) {

        }
        EXECUTE_TORRENT_COMMAND = str;
    }

    public static final String TELEGRAM_OUTPUT_TEXT;
    static {
        String str = "TELEGRAM_OUTPUT_TEXT";
        try {
            str = System.getenv("TELEGRAM_OUTPUT_TEXT");
        } catch (Exception ignore) {

        }
        TELEGRAM_OUTPUT_TEXT = str;
    }

    @Override
    protected void getDeliverCallbackPrivate(RabbitMessage rabbitMessage) {
        try {
            String text = rabbitMessage.getText();
            long userId = rabbitMessage.getUserId();

            List<String> result = ExecuteBash.executeViaCLI(text);
            RabbitUtils.postMessage(userId, text + " !NOT EXECUTED. TODO!", TELEGRAM_OUTPUT_TEXT);
        } catch (Exception e) {
            System.out.println("During message handle got an error: " + e.getMessage());
        }
    }

    @Override
    protected String getQueue() {
        return EXECUTE_TORRENT_COMMAND;
    }
}
