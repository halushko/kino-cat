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
    public static final String DIR_TORRENT_WATCH= System.getenv("DIR_TORRENT_WATCH");
    public static final String   EXECUTE_TORRENT_COMMAND_QUEUE= System.getenv("  EXECUTE_TORRENT_COMMAND_QUEUE");

    @Override
    protected void getDeliverCallbackPrivate(RabbitMessage rabbitMessage) {
        try {
            URL fileUrl = new URL(FILE_URL_PREFIX + rabbitMessage.getValue(FILE_PATH));
            long userId = rabbitMessage.getUserId();
            String fileName = rabbitMessage.getValue(FILE_NAME);

            File localFile = new File("/home/torrent_files/" + fileName);
            try (InputStream is = fileUrl.openStream()) {
                FileUtils.copyInputStreamToFile(is, localFile);
                RabbitUtils.postMessage(userId, "/start_torrent " + DIR_TORRENT_WATCH + "/" + fileName,   EXECUTE_TORRENT_COMMAND_QUEUE);
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