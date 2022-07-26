package com.halushko;

import com.halushko.rabKot.handlers.input.InputMessageHandler;
import com.halushko.rabKot.rabbit.RabbitMessage;
import com.halushko.rabKot.rabbit.RabbitUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import static com.halushko.rabKot.rabbit.RabbitMessage.KEYS.FILE_NAME;
import static com.halushko.rabKot.rabbit.RabbitMessage.KEYS.FILE_PATH;

public class UserMessageHandler extends InputMessageHandler {
    private static final String FILE_URL_PREFIX = "https://api.telegram.org/file/bot" + System.getenv("BOT_TOKEN") + "/";

    public static final String TELEGRAM_OUTPUT_TEXT_QUEUE = System.getenv("TELEGRAM_OUTPUT_TEXT_QUEUE");
    public static final String TELEGRAM_INPUT_FILE_QUEUE= System.getenv("TELEGRAM_INPUT_FILE_QUEUE");

    //    static {
//        String str = "TELEGRAM_OUTPUT_TEXT_QUEUE";
//        try {
//            String str1 = System.getenv("TELEGRAM_OUTPUT_TEXT_QUEUE");
//            if (!(str1 == null || str1.equals("") || str1.equalsIgnoreCase("null"))) {
//                str = str1;
//            }
//            str = str1;
//        } catch (Exception ignore) {
//        }
//        TELEGRAM_OUTPUT_TEXT_QUEUE = str;
//    }
//    static {
//        String str = "TELEGRAM_INPUT_FILE_QUEUE";
//        try {
//            String str1 = System.getenv("TELEGRAM_INPUT_FILE_QUEUE");
//            if (!(str1 == null || str1.equals("") || str1.equalsIgnoreCase("null"))) {
//                str = str1;
//            }
//            str =str1;
//        } catch (Exception ignore) {
//        }
//        TELEGRAM_INPUT_FILE_QUEUE = str;
//    }


    @Override
    protected void getDeliverCallbackPrivate(RabbitMessage rabbitMessage) {
        try {
            URL fileUrl = new URL(FILE_URL_PREFIX + rabbitMessage.getValue(FILE_PATH));
            long userId = rabbitMessage.getUserId();
            String fileName = rabbitMessage.getValue(FILE_NAME);

            File localFile = new File("/home/torrent_files/" + fileName);
            try (InputStream is = fileUrl.openStream()) {
                FileUtils.copyInputStreamToFile(is, localFile);
            } catch (IOException e) {
                RabbitUtils.postMessage(userId, e.getMessage(), TELEGRAM_OUTPUT_TEXT_QUEUE);
            }
        } catch (Exception e) {
            System.out.println("During message handle got an error: " + e.getMessage());
        }
    }

    @Override
    protected String getQueue() {
        return TELEGRAM_INPUT_FILE_QUEUE;
    }
}