package com.halushko.kinocat.fileConsumer;

import com.halushko.kinocat.middleware.cli.Constants;
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
    private static final String FILE_URL_PREFIX = String.format("%s%s/", "https://api.telegram.org/file/bot", System.getenv("BOT_TOKEN"));
    public static final String DIR_TORRENT_WATCH = System.getenv("DIR_TORRENT_WATCH");

    @Override
    protected void getDeliverCallbackPrivate(RabbitMessage rabbitMessage) {
        try {
            URL fileUrl = new URL(FILE_URL_PREFIX + rabbitMessage.getValue(FILE_PATH));
            long userId = rabbitMessage.getUserId();
            String fileName = rabbitMessage.getValue(FILE_NAME);

            File localFile = new File("/home/torrent_files/" + fileName);
            try (InputStream is = fileUrl.openStream()) {
                FileUtils.copyInputStreamToFile(is, localFile);
                RabbitUtils.postMessage(userId, String.format("%s %s%s", Constants.Commands.Torrent.START_TORRENT_FILE, DIR_TORRENT_WATCH, fileName), Constants.Queues.Torrent.EXECUTE_VOID_TORRENT_COMMAND);
            } catch (IOException e) {
                RabbitUtils.postMessage(userId, e.getMessage(), Constants.Queues.Telegram.TELEGRAM_OUTPUT_TEXT);
            }
        } catch (Exception e) {
            Logger.getRootLogger().error("During message handle got an error: ", e);
        }
    }

    @Override
    protected String getQueue() {
        return Constants.Queues.Telegram.TELEGRAM_INPUT_FILE;
    }
}