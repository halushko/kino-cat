package com.halushko.kinocat.bot.handlers.input;

import com.halushko.kinocat.middleware.handlers.input.InputMessageHandler;
import com.halushko.kinocat.middleware.rabbit.RabbitMessage;
import org.apache.log4j.Logger;

import static com.halushko.kinocat.bot.KoTorrentBot.sendText;
import static com.halushko.kinocat.middleware.rabbit.RabbitJson.unNormalizeText;

public class SendTextMessageToUser extends InputMessageHandler {
    public static final String TELEGRAM_OUTPUT_TEXT_QUEUE = System.getenv("TELEGRAM_OUTPUT_TEXT_QUEUE");
    @Override
    protected void getDeliverCallbackPrivate(RabbitMessage message) {
        long chatId = message.getUserId();
        String text = unNormalizeText(message.getText());
        Logger.getRootLogger().debug(String.format("[SendTextMessageToUser] Send text chatId:%s, text:%s", chatId, text));
        sendText(chatId, text);
    }
    @Override
    protected String getQueue() {
        return TELEGRAM_OUTPUT_TEXT_QUEUE;
    }
}
