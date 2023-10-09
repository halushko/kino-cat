package com.halushko.kinocat.bot.handlers.telegram;

import com.halushko.kinocat.core.cli.Constants;
import com.halushko.kinocat.core.handlers.telegram.UserMessageHandler;
import com.halushko.kinocat.core.rabbit.RabbitJson;
import com.halushko.kinocat.core.rabbit.RabbitMessage;
import com.halushko.kinocat.core.rabbit.RabbitUtils;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
public class TextHandler extends UserMessageHandler {
    @Override
    protected void readMessagePrivate(Update update) {
        long chatId = update.getMessage().getChatId();
        String message = RabbitJson.normalizedValue(update.getMessage().getText());

        log.debug(String.format("[TextHandler] chatId:%s, message:%s", chatId, message));

        RabbitMessage rm = new RabbitMessage(chatId, message);
        RabbitUtils.postMessage(rm, Constants.Queues.Telegram.TELEGRAM_INPUT_TEXT);
    }

    @Override
    protected boolean validate(Update update) {
        return update != null && update.getMessage().hasText();
    }
}
