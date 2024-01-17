package com.halushko.kinocat.file;

import com.halushko.kinocat.core.commands.Constants;
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
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import static com.halushko.kinocat.core.rabbit.SmartJson.KEYS.*;
@Slf4j
public class UserMessageHandler extends InputMessageHandler {
    private final static String FILE_URL_PREFIX = String.format("%s%s/", "https://api.telegram.org/file/bot", System.getenv("BOT_TOKEN"));

    @SuppressWarnings("unchecked")
    private final static Set<String> folders = new LinkedHashSet<>() {{
        new SmartJson(System.getenv("TORRENT_IP"))
                .convertToList()
                .stream()
                .map((x -> (Map<String, Object>) x))
                .forEach(x -> add(String.valueOf(x.getOrDefault("name", "default")).toLowerCase()));
    }};

    @Override
    protected String getDeliverCallbackPrivate(SmartJson rabbitMessage) {
        try {
            String mimeType = rabbitMessage.getValue(MIME_TYPE);
            if ("application/x-bittorrent".equalsIgnoreCase(mimeType)) {
                handleTorrent(rabbitMessage);
            }
        } catch (Exception e) {
            log.error("During message handle got an error: ", e);
        }
        return "";
    }

    protected void handleTorrent(SmartJson rm) throws MalformedURLException {
        long fileSize = Long.parseLong(rm.getValue(SIZE));
        if (fileSize > 5242880L) {
            log.warn(String.format("The file size is too big for .torrent (more than 0.5 Mb). Size = %s bytes", fileSize));
            return;
        }
        URL fileUrl = java.net.URI.create(FILE_URL_PREFIX + rm.getValue(FILE_PATH)).toURL();
        long userId = rm.getUserId();
        String fileName = String.format("%s%s", rm.getValue(FILE_ID), ".torrent");
        String message = rm.getValue(CAPTION);
        if(message == null || message.trim().isEmpty()) {
            message = "";
        } else if(folders.contains(message.trim().toLowerCase())){
            message = "_" + message.trim().toLowerCase();
        } else {
            message = "";
        }
        File localFile = new File(String.format("/home/torrent_files%s/%s", message, fileName));
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