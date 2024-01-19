package com.halushko.kinocat.file;

import com.halushko.kinocat.core.JsonConstants.SmartJsonKeys;
import com.halushko.kinocat.core.JsonConstants.WebKeys;
import com.halushko.kinocat.core.Queues;
import com.halushko.kinocat.core.handlers.input.InputMessageHandler;
import com.halushko.kinocat.core.rabbit.RabbitUtils;
import com.halushko.kinocat.core.rabbit.SmartJson;
import com.halushko.kinocat.core.prcessors.ServicesInfoProcessor;
import com.halushko.kinocat.core.prcessors.ValueProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class UserMessageHandler extends InputMessageHandler {
    private final static List<String> folders = new FoldersProcessor(System.getenv("TORRENT_IP")).values.keySet().stream().toList();
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
        if (fileSize > 5242880L) {
            log.warn(String.format("The file size is too big for .torrent (more than 0.5 Mb). Size = %s bytes", fileSize));
            return;
        }
        URL fileUrl = java.net.URI.create(FILE_URL_PREFIX + rm.getValue(SmartJsonKeys.FILE_PATH)).toURL();
        long userId = rm.getUserId();
        String fileName = String.format("%s%s", rm.getValue(SmartJsonKeys.FILE_ID), ".torrent");
        String message = rm.getValue(SmartJsonKeys.CAPTION);

        if(message == null || message.trim().isEmpty()) {
            message = "";
        }
        message = message.trim().toLowerCase();
        message = folders.contains(message) ? "_" + message : "";

        File localFile = new File(String.format("/home/torrent_files%s/%s", message, fileName));
        try (InputStream is = fileUrl.openStream()) {
            FileUtils.copyInputStreamToFile(is, localFile);
        } catch (IOException e) {
            RabbitUtils.postMessage(userId, e.getMessage(), Queues.Telegram.TELEGRAM_OUTPUT_TEXT);
        }
    }

    @Override
    protected String getQueue() {
        return Queues.Telegram.TELEGRAM_INPUT_FILE;
    }

    private static class FoldersProcessor extends ServicesInfoProcessor{

        public FoldersProcessor(String json) {
            super(json);
        }

        @Override
        public ValueProcessor getNameProcessor() {
            return new ValueProcessor(WebKeys.KEY_NAME, "");
        }

        @Override
        public List<ValueProcessor> getServiceProcessors() {
            return new ArrayList<>();
        }

        @Override
        public String getUrlTemplate() {
            return "";
        }
    }
}