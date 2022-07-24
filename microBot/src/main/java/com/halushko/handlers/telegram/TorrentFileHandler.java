package com.halushko.handlers.telegram;

import com.halushko.KoTorrentBot;
import com.halushko.rabKot.rabbit.RabbitMessage;
import com.halushko.rabKot.rabbit.RabbitUtils;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.halushko.rabKot.rabbit.RabbitMessage.KEYS.*;

public class TorrentFileHandler extends KoTorrentUserMessageHandler {
    public static final String TELEGRAM_INPUT_FILE_QUEUE;
    static {
        String str = System.getenv("TELEGRAM_INPUT_FILE_QUEUE");
        TELEGRAM_INPUT_FILE_QUEUE = str != null ? str : "TELEGRAM_INPUT_FILE_QUEUE";
    }

    public static final String ASD;
    static {
        String str = "ASD";
        try {
            str = System.getenv("ASD");
        } catch (Exception ignore) {

        }
        ASD = str;
    }
    @Override
    protected void readMessagePrivate(Update update) throws IOException, TimeoutException {
        String uploadedFileId = update.getMessage().getDocument().getFileId();
        GetFile uploadedFile = new GetFile();
        uploadedFile.setFileId(uploadedFileId);

        try {
            RabbitMessage rm = new RabbitMessage(update.getMessage().getChatId());
            rm.addValue(FILE_PATH, KoTorrentBot.BOT.execute(uploadedFile).getFilePath());
            rm.addValue(FILE_NAME, update.getMessage().getDocument().getFileName());
            rm.addValue(TEXT, update.getMessage().getText());
            RabbitUtils.postMessage(rm, TELEGRAM_INPUT_FILE_QUEUE);
        } catch (TelegramApiException e) {
            KoTorrentBot.sendText(update.getMessage().getChatId(), e.getMessage());
        }
    }

    @Override
    protected boolean validate(Update update) {
        return update != null && update.getMessage().hasDocument();
    }
}
