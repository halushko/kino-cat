package com.halushko.kinocat.fileConsumer;

import com.halushko.kinocat.middleware.handlers.input.InputMessageHandler;
import com.halushko.kinocat.middleware.rabbit.RabbitMessage;
import com.halushko.kinocat.middleware.rabbit.RabbitUtils;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import static com.halushko.kinocat.middleware.rabbit.RabbitMessage.KEYS.FILE_NAME;
import static com.halushko.kinocat.middleware.rabbit.RabbitMessage.KEYS.FILE_PATH;

public class UserMessageHandler extends InputMessageHandler {
    private static final String FILE_URL_PREFIX = "https://api.telegram.org/file/bot" + System.getenv("BOT_TOKEN") + "/";

    public static final String TELEGRAM_OUTPUT_TEXT_QUEUE = System.getenv("TELEGRAM_OUTPUT_TEXT_QUEUE");
    public static final String TELEGRAM_INPUT_FILE_QUEUE= System.getenv("TELEGRAM_INPUT_FILE_QUEUE");
    public static final String DIR_TORRENT_WATCH= System.getenv("DIR_TORRENT_WATCH");
    public static final String TELEGRAM_INPUT_TEXT_QUEUE= System.getenv("TELEGRAM_INPUT_TEXT_QUEUE");

    @Override
    protected void getDeliverCallbackPrivate(RabbitMessage rabbitMessage) {
        try {
            URL fileUrl = new URL(FILE_URL_PREFIX + rabbitMessage.getValue(FILE_PATH));
            long userId = rabbitMessage.getUserId();
            String fileName = rabbitMessage.getValue(FILE_NAME);

            File localFile = new File("/home/torrent_files/" + fileName);
            try (InputStream is = fileUrl.openStream()) {
                FileUtils.copyInputStreamToFile(is, localFile);
                RabbitUtils.postMessage(userId, "/start_torrent " + DIR_TORRENT_WATCH + fileName, TELEGRAM_INPUT_TEXT_QUEUE);
            } catch (IOException e) {
                RabbitUtils.postMessage(userId, e.getMessage(), TELEGRAM_OUTPUT_TEXT_QUEUE);
            }
        } catch (Exception e) {
            Logger.getRootLogger().error("During message handle got an error: ", e);
        }
    }

    @Override
    protected String getQueue() {
        return TELEGRAM_INPUT_FILE_QUEUE;
    }
}