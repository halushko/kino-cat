package com.halushko.kinocat.bot.handlers.telegram;

import com.halushko.kinocat.bot.KoTorrentBot;
import com.halushko.kinocat.core.JsonConstants.SmartJsonKeys;
import com.halushko.kinocat.core.Queues;
import com.halushko.kinocat.core.handlers.telegram.UserMessageHandler;
import com.halushko.kinocat.core.rabbit.SmartJson;
import com.halushko.kinocat.core.rabbit.RabbitUtils;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
public class TorrentFileHandler extends UserMessageHandler {
    @Override
    protected void readMessagePrivate(Update update) {
        long chatId = update.getMessage().getChatId();
        String uploadedFileId = update.getMessage().getDocument().getFileId();
        long fileSize = update.getMessage().getDocument().getFileSize();
        String mimeType = update.getMessage().getDocument().getMimeType();
        String message = update.getMessage().getText();
        String caption = update.getMessage().getCaption();
        log.debug(
                String.format("[TorrentFileHandler] chatId:%s, uploadedFileId:%s, fileName:%s, message:%s, caption:%s",
                        chatId, uploadedFileId, update.getMessage().getDocument().getFileName(), message, caption
                )
        );

        GetFile uploadedFile = new GetFile();
        uploadedFile.setFileId(uploadedFileId);

        try {
            SmartJson rm = new SmartJson(chatId).
                    addValue(SmartJsonKeys.FILE_PATH, KoTorrentBot.BOT.execute(uploadedFile).getFilePath()).
                    addValue(SmartJsonKeys.FILE_ID, update.getMessage().getDocument().getFileId()).
                    addValue(SmartJsonKeys.TEXT, message).
                    addValue(SmartJsonKeys.CAPTION, caption).
                    addValue(SmartJsonKeys.SIZE, String.valueOf(fileSize)).
                    addValue(SmartJsonKeys.MIME_TYPE, mimeType);

            RabbitUtils.postMessage(rm, Queues.Telegram.TELEGRAM_INPUT_FILE);
        } catch (TelegramApiException e) {
            log.error("[TorrentFileHandler] Error during file processing", e);
            KoTorrentBot.sendText(chatId, e.getMessage());
        }
    }

    @Override
    protected boolean validate(Update update) {
        return update != null && update.getMessage().hasDocument() && update.getMessage().getDocument().getFileName().toLowerCase().endsWith(".torrent");
    }
}
