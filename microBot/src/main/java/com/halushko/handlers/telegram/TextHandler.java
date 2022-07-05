package com.halushko.handlers.telegram;

import com.halushko.rabKot.rabbit.RabbitUtils;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

class TextHandler extends KoTorrentUserMessageHandler {
    @Override
    protected void readMessagePrivate(Update update) throws IOException, TimeoutException {
        RabbitUtils.postMessage(update.getMessage().getChatId(), update.getMessage().getText(), "TELEGRAM_INPUT_TEXT");
    }

    @Override
    protected boolean validate(Update update) {
        return update != null && update.getMessage().hasText();
    }
}
