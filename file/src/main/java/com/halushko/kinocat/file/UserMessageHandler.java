package com.halushko.kinocat.file;

import com.halushko.kinocat.core.cli.Command;
import com.halushko.kinocat.core.cli.Constants;
import com.halushko.kinocat.core.cli.ScriptsCollection;
import com.halushko.kinocat.core.handlers.input.InputMessageHandler;
import com.halushko.kinocat.core.rabbit.RabbitMessage;
import com.halushko.kinocat.core.rabbit.RabbitUtils;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class UserMessageHandler extends InputMessageHandler {
    private static final String FILE_URL_PREFIX = String.format("%s%s/", "https://api.telegram.org/file/bot", System.getenv("BOT_TOKEN"));

    private static final ScriptsCollection scripts = new ScriptsCollection() {{
        addValue(Constants.Commands.Torrent.START_TORRENT_FILE, "start_torrent.sh", Constants.Queues.Torrent.EXECUTE_VOID_TORRENT_COMMAND);
    }};

    @Override
    protected void getDeliverCallbackPrivate(RabbitMessage rabbitMessage) {
        try {
            String mimeType = rabbitMessage.getValue("MIME_TYPE");
            if ("application/x-bittorrent".equalsIgnoreCase(mimeType)) {
                handleTorrent(rabbitMessage);
            }
        } catch (Exception e) {
            Logger.getRootLogger().error("During message handle got an error: ", e);
        }
    }

    protected void handleTorrent(RabbitMessage rm) throws MalformedURLException {
        long fileSize = Long.parseLong(rm.getValue("SIZE"));
        if (fileSize > 5242880L) {
            Logger.getRootLogger().warn(String.format("The file size is too big for .torrent (more than 0.5 Mb). Size = %s bytes", fileSize));
            return;
        }
        URL fileUrl = new URL(FILE_URL_PREFIX + rm.getValue(RabbitMessage.KEYS.FILE_PATH));
        long userId = rm.getUserId();
        String fileName = String.format("%s%s", rm.getValue("FILE_ID"), ".torrent");

        File localFile = new File("/home/torrent_files/" + fileName);
        try (InputStream is = fileUrl.openStream()) {
            FileUtils.copyInputStreamToFile(is, localFile);
            Command command = scripts.getCommand(Constants.Commands.Torrent.START_TORRENT_FILE);
            RabbitUtils.postMessage(userId, String.format("%s %s%s", command.getScript(), "/home/media/torrent/torrent_files/", fileName), command.getQueue());
        } catch (IOException e) {
            RabbitUtils.postMessage(userId, e.getMessage(), Constants.Queues.Telegram.TELEGRAM_OUTPUT_TEXT);
        }
    }

    @Override
    protected String getQueue() {
        return Constants.Queues.Telegram.TELEGRAM_INPUT_FILE;
    }
}