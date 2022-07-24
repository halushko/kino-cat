package com.halushko.handlers.telegram;

import com.halushko.rabKot.rabbit.RabbitUtils;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

class TextHandler extends KoTorrentUserMessageHandler {
    private final static String TELEGRAM_INPUT_TEXT_QUEUE;

    static {
        String str = System.getenv("TELEGRAM_INPUT_TEXT_QUEUE");
        TELEGRAM_INPUT_TEXT_QUEUE = str != null ? str : "TELEGRAM_INPUT_TEXT_QUEUE";
    }
    @Override
    protected void readMessagePrivate(Update update) throws IOException, TimeoutException {
        RabbitUtils.postMessage(update.getMessage().getChatId(), update.getMessage().getText(), TELEGRAM_INPUT_TEXT_QUEUE);
    }

    @Override
    protected boolean validate(Update update) {
        return update != null && update.getMessage().hasText();
    }
}
