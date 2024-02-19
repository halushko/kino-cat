package com.halushko.kinocat.bot.handlers.telegram;

import com.halushko.kinocat.core.Queues;
import com.halushko.kinocat.core.handlers.telegram.UserMessageHandler;
import com.halushko.kinocat.core.rabbit.RabbitUtils;
import com.halushko.kinocat.core.rabbit.SmartJson;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
public class TextHandler extends UserMessageHandler {
    @Override
    protected void readMessagePrivate(Update update) {
        long chatId = update.getMessage().getChatId();
        String message = update.getMessage().getText();

        log.debug("[TextHandler] chatId:{}, message:{}", chatId, message);

        SmartJson rm = new SmartJson(chatId, message);
        RabbitUtils.postMessage(rm, Queues.Telegram.TELEGRAM_INPUT_TEXT);
    }

    @Override
    protected boolean validate(Update update) {
        return update != null && update.getMessage().hasText();
    }
}
