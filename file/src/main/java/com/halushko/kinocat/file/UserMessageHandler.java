package com.halushko.kinocat.file;

import com.halushko.kinocat.core.cli.Command;
import com.halushko.kinocat.core.cli.Constants;
import com.halushko.kinocat.core.cli.ScriptsCollection;
import com.halushko.kinocat.core.handlers.input.InputMessageHandler;
import com.halushko.kinocat.core.rabbit.SmartJson;
import com.halushko.kinocat.core.rabbit.RabbitUtils;
import org.apache.commons.io.FileUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

@Slf4j
public class UserMessageHandler extends InputMessageHandler {
    private static final String FILE_URL_PREFIX = String.format("%s%s/", "https://api.telegram.org/file/bot", System.getenv("BOT_TOKEN"));

    @Override
    protected void getDeliverCallbackPrivate(SmartJson rabbitMessage) {
        try {
            String mimeType = rabbitMessage.getValue("MIME_TYPE");
            if ("application/x-bittorrent".equalsIgnoreCase(mimeType)) {
                handleTorrent(rabbitMessage);
            }
        } catch (Exception e) {
            log.error("During message handle got an error: ", e);
        }
    }

    protected void handleTorrent(SmartJson rm) throws MalformedURLException {
        long fileSize = Long.parseLong(rm.getValue("SIZE"));
        if (fileSize > 5242880L) {
            log.warn(String.format("The file size is too big for .torrent (more than 0.5 Mb). Size = %s bytes", fileSize));
            return;
        }
        URL fileUrl = java.net.URI.create(FILE_URL_PREFIX + rm.getValue(SmartJson.KEYS.FILE_PATH)).toURL();
        long userId = rm.getUserId();
        String fileName = String.format("%s%s", rm.getValue("FILE_ID"), ".torrent");

        File localFile = new File("/home/torrent_files/" + fileName);
        try (InputStream is = fileUrl.openStream()) {
            FileUtils.copyInputStreamToFile(is, localFile);
        } catch (IOException e) {
            RabbitUtils.postMessage(userId, e.getMessage(), Constants.Queues.Telegram.TELEGRAM_OUTPUT_TEXT);
        }
    }

    @Override
    protected String getQueue() {
        return Constants.Queues.Telegram.TELEGRAM_INPUT_FILE;
    }
}