package com.halushko.handlers.telegram;

import com.halushko.rabKot.handlers.telegram.UserMessageHandler;
import com.halushko.rabKot.rabbit.RabbitMessage;
import com.halushko.rabKot.rabbit.RabbitUtils;
import org.apache.log4j.Logger;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.halushko.rabKot.rabbit.RabbitJson.normalizedValue;

public class TextHandler extends UserMessageHandler {
    public static final String TELEGRAM_INPUT_TEXT_QUEUE = System.getenv("TELEGRAM_INPUT_TEXT_QUEUE");

    @Override
    protected void readMessagePrivate(Update update) {
        long chatId = update.getMessage().getChatId();
        String message = normalizedValue(update.getMessage().getText());

        Logger.getRootLogger().debug(String.format("[TextHandler] chatId:%s, message:%s", chatId, message));

        RabbitMessage rm = new RabbitMessage(chatId, message);
        RabbitUtils.postMessage(rm, TELEGRAM_INPUT_TEXT_QUEUE);
    }

    @Override
    protected boolean validate(Update update) {
        return update != null && update.getMessage().hasText();
    }
}
