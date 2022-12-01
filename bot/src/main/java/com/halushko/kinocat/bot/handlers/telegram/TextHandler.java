package com.halushko.kinocat.bot.handlers.telegram;

import com.halushko.kinocat.middleware.handlers.telegram.UserMessageHandler;
import com.halushko.kinocat.middleware.rabbit.RabbitJson;
import com.halushko.kinocat.middleware.rabbit.RabbitMessage;
import com.halushko.kinocat.middleware.rabbit.RabbitUtils;
import org.apache.log4j.Logger;
import org.telegram.telegrambots.meta.api.objects.Update;

public class TextHandler extends UserMessageHandler {
    public static final String TELEGRAM_INPUT_TEXT_QUEUE = System.getenv("TELEGRAM_INPUT_TEXT_QUEUE");

    @Override
    protected void readMessagePrivate(Update update) {
        long chatId = update.getMessage().getChatId();
        String message = RabbitJson.normalizedValue(update.getMessage().getText());

        Logger.getRootLogger().debug(String.format("[TextHandler] chatId:%s, message:%s", chatId, message));

        RabbitMessage rm = new RabbitMessage(chatId, message);
        RabbitUtils.postMessage(rm, TELEGRAM_INPUT_TEXT_QUEUE);
    }

    @Override
    protected boolean validate(Update update) {
        return update != null && update.getMessage().hasText();
    }
}
