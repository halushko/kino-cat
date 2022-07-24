package com.halushko;

import com.halushko.rabKot.handlers.input.InputMessageHandler;
import com.halushko.rabKot.rabbit.RabbitMessage;
import com.halushko.rabKot.cli.ExecuteBash;
import com.halushko.rabKot.rabbit.RabbitUtils;

import java.util.List;

public class TorrentOperator extends InputMessageHandler {
    public static final String EXECUTE_TORRENT_COMMAND_QUEUE = System.getenv("EXECUTE_TORRENT_COMMAND_QUEUE");
    public static final String TELEGRAM_OUTPUT_TEXT = System.getenv("TELEGRAM_OUTPUT_TEXT_QUEUE");

//    static {
//        String str = "EXECUTE_TORRENT_COMMAND_QUEUE";
//        try {
//            String str1 = System.getenv("EXECUTE_TORRENT_COMMAND_QUEUE");
//            if (!(str1 == null || str1.equals("") || str1.equalsIgnoreCase("null"))) {
//                str = str1;
//            }
//        } catch (Exception ignore) {
//        }
//        EXECUTE_TORRENT_COMMAND_QUEUE = str;
//    }
//    static {
//        String str = "TELEGRAM_OUTPUT_TEXT";
//        try {
//            String str1 = System.getenv("TELEGRAM_OUTPUT_TEXT_QUEUE");
//            if (!(str1 == null || str1.equals("") || str1.equalsIgnoreCase("null"))) {
//                str = str1;
//            }
//            str = str1;
//        } catch (Exception ignore) {
//        }
//        TELEGRAM_OUTPUT_TEXT = str;
//    }

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
        return EXECUTE_TORRENT_COMMAND_QUEUE;
    }
}
