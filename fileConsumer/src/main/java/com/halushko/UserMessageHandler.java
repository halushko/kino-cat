package com.halushko;

import com.halushko.rabKot.handlers.input.InputMessageHandler;
import com.halushko.rabKot.rabbit.RabbitMessage;
import com.halushko.rabKot.rabbit.RabbitUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import static com.halushko.Main.TELEGRAM_OUTPUT_TEXT_QUEUE;

public class UserMessageHandler extends InputMessageHandler {
    private static final String FILE_URL_PREFIX = "https://api.telegram.org/file/bot" + System.getenv("BOT_TOKEN") + "/";

    @Override
    protected void getDeliverCallbackPrivate(RabbitMessage rabbitMessage) {
        try {
            String uploadedFilePath = rabbitMessage.getText();
            URL fileUrl = new URL(FILE_URL_PREFIX + uploadedFilePath);
            long userId = rabbitMessage.getUserId();

            File localFile = new File("/home/media/torrent/torrent_files/" + fileUrl.getFile());
            try(InputStream is = fileUrl.openStream()) {
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
        return System.getenv("TELEGRAM_INPUT_FILE_QUEUE");
    }
}