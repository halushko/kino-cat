package com.halushko.torrent.parsers;

import halushko.cw3.bot.vareshka.BotController;
import org.apache.commons.io.FileUtils;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class DownloadFile {
    protected final Update update;

    public DownloadFile(Update update) {
        super();
        this.update = update;
    }
    public void run() {
        String uploadedFileId = update.getMessage().getDocument().getFileId();
        GetFile uploadedFile = new GetFile();
        uploadedFile.setFileId(uploadedFileId);

        String uploadedFilePath = null;
        try {
            uploadedFilePath = BotController.BOT.execute(uploadedFile).getFilePath();
        } catch (TelegramApiException e) {
            e.printStackTrace();
            return;
        }
        File localFile = new File("/media/torrent/torrent_files/" + update.getMessage().getDocument().getFileName());
        InputStream is = null;
        try {
            is = new URL("https://api.telegram.org/file/bot" + BotController.BOT.getBotToken() + "/" + uploadedFilePath).openStream();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        try {
            FileUtils.copyInputStreamToFile(is, localFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
