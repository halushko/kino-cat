package com.halushko.handlers.telegram;

import com.halushko.KoTorrentBot;
import com.halushko.rabKot.handlers.telegram.UserMessageHandler;
import com.halushko.rabKot.rabbit.RabbitMessage;
import com.halushko.rabKot.rabbit.RabbitUtils;
import org.apache.log4j.Logger;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static com.halushko.rabKot.rabbit.RabbitMessage.KEYS.*;

public class TorrentFileHandler extends UserMessageHandler {
    public static final String TELEGRAM_INPUT_FILE_QUEUE = System.getenv("TELEGRAM_INPUT_FILE_QUEUE");
    @Override
    protected void readMessagePrivate(Update update) {
        long chatId = update.getMessage().getChatId();
        String uploadedFileId = update.getMessage().getDocument().getFileId();
        String fileName = update.getMessage().getDocument().getFileName();
        String message = update.getMessage().getText();
        String caption = update.getMessage().getCaption();
        Logger.getRootLogger().debug(
                String.format("[TorrentFileHandler] chatId:%s, uploadedFileId:%s, fileName:%s, message:%s, caption:%s",
                        chatId, uploadedFileId, fileName, message, caption
                )
        );

        GetFile uploadedFile = new GetFile();
        uploadedFile.setFileId(uploadedFileId);

        try {
            RabbitMessage rm = new RabbitMessage(chatId);
            rm.addValue(FILE_PATH, KoTorrentBot.BOT.execute(uploadedFile).getFilePath());
            rm.addValue(FILE_NAME, fileName);
            rm.addValue(TEXT, message);
            rm.addValue("CAPTION", caption);
            RabbitUtils.postMessage(rm, TELEGRAM_INPUT_FILE_QUEUE);
        } catch (TelegramApiException e) {
            Logger.getRootLogger().error("[TorrentFileHandler] Error during file processing", e);
            KoTorrentBot.sendText(chatId, e.getMessage());
        }
    }

    @Override
    protected boolean validate(Update update) {
        return update != null && update.getMessage().hasDocument();
    }
}
