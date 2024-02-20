package com.halushko.kinocat.file;

import com.halushko.kinocat.core.JsonConstants.SmartJsonKeys;
import com.halushko.kinocat.core.Queues;
import com.halushko.kinocat.core.handlers.input.InputMessageHandler;
import com.halushko.kinocat.core.rabbit.RabbitUtils;
import com.halushko.kinocat.core.rabbit.SmartJson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

@Slf4j
public class UserMessageHandler extends InputMessageHandler {
    private final static String FILE_URL_PREFIX = String.format("%s%s/", "https://api.telegram.org/file/bot", System.getenv("BOT_TOKEN"));

    @Override
    protected String getDeliverCallbackPrivate(SmartJson rabbitMessage) {
        try {
            String mimeType = rabbitMessage.getValue(SmartJsonKeys.MIME_TYPE);
            if ("application/x-bittorrent".equalsIgnoreCase(mimeType)) {
                handleTorrent(rabbitMessage);
            }
        } catch (Exception e) {
            log.error("During message handle got an error: ", e);
        }
        return "";
    }

    protected void handleTorrent(SmartJson rm) throws MalformedURLException {
        long fileSize = Long.parseLong(rm.getValue(SmartJsonKeys.SIZE));
        long userId = rm.getUserId();
        if (fileSize > 5242880L) {
            String error = String.format("The file size is too big for .torrent (more than 5 Mb). Size = %s Mb", ((double) fileSize) / (1024 * 1024));
            log.warn(error);
            RabbitUtils.postMessage(userId, error, Queues.Telegram.TELEGRAM_OUTPUT_TEXT);
            return;
        }
        URL fileUrl = java.net.URI.create(FILE_URL_PREFIX + rm.getValue(SmartJsonKeys.FILE_PATH)).toURL();
        String fileName = String.format("%s%s", rm.getValue(SmartJsonKeys.FILE_ID), ".torrent");

        if (Constants.FOLDERS.size() > 1) {
            sendToChoose(userId, fileUrl);
        } else {
            sendToDownload(fileName, fileUrl, userId);
        }
    }

    private static void sendToDownload(String fileName, URL fileUrl, long userId) {
        File localFile = new File(String.format("%s/%s", Constants.PATH_TO_DESTINATION_FOLDER, fileName));
        try (InputStream is = fileUrl.openStream()) {
            FileUtils.copyInputStreamToFile(is, localFile);
        } catch (IOException e) {
            RabbitUtils.postMessage(userId, e.getMessage(), Queues.Telegram.TELEGRAM_OUTPUT_TEXT);
        }
    }

    private static void sendToChoose(long userId, URL fileUrl) {
        String fileName = fileUrl.getFile().replaceAll(".*/", "");
        File localFile = new File(String.format("%s/%s", Constants.PATH_TO_UNAPPROVED_FOLDER, fileName));
        try (InputStream is = fileUrl.openStream()) {
            FileUtils.copyInputStreamToFile(is, localFile);
            SmartJson message = new SmartJson(userId)
                    .addValue(SmartJsonKeys.FILE_PATH, localFile.getAbsolutePath())
                    .addValue(SmartJsonKeys.FILE_ID, fileName);
            RabbitUtils.postMessage(message, Queues.File.CHOOSE_THE_DESTINATION);
        } catch (IOException e) {
            RabbitUtils.postMessage(userId, e.getMessage(), Queues.Telegram.TELEGRAM_OUTPUT_TEXT);
        }
    }

    @Override
    protected String getQueue() {
        return Queues.Telegram.TELEGRAM_INPUT_FILE;
    }
}