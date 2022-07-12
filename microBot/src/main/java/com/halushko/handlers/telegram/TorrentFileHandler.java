package com.halushko.handlers.telegram;

import com.halushko.KoTorrentBot;
import com.halushko.rabKot.rabbit.RabbitUtils;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.TimeoutException;

public class TorrentFileHandler extends KoTorrentUserMessageHandler {
    @Override
    protected void readMessagePrivate(Update update) throws IOException, TimeoutException {
        String uploadedFileId = update.getMessage().getDocument().getFileId();
        GetFile uploadedFile = new GetFile();
        uploadedFile.setFileId(uploadedFileId);

        try {
            RabbitUtils.postMessage(update.getMessage().getChatId(),
                    KoTorrentBot.BOT.execute(uploadedFile).getFilePath(),
                    System.getenv("TELEGRAM_INPUT_FILE_QUEUE")
            );
        } catch (TelegramApiException e) {
            KoTorrentBot.sendText(update.getMessage().getChatId(), e.getMessage());
        }
    }

    @Override
    protected boolean validate(Update update) {
        return update != null && update.getMessage().hasDocument();
    }
}
