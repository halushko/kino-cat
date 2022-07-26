package com.halushko.handlers.telegram;

import com.halushko.rabKot.handlers.telegram.UserMessageHandler;
import com.halushko.rabKot.rabbit.RabbitMessage;
import com.halushko.rabKot.rabbit.RabbitUtils;
import org.telegram.telegrambots.meta.api.objects.Update;

public class TextHandler extends UserMessageHandler {
    public static final String TELEGRAM_INPUT_TEXT_QUEUE = System.getenv("TELEGRAM_INPUT_TEXT_QUEUE");

    @Override
    protected void readMessagePrivate(Update update) {
        RabbitMessage rm = new RabbitMessage(update.getMessage().getChatId(), update.getMessage().getText());
        RabbitUtils.postMessage(rm, TELEGRAM_INPUT_TEXT_QUEUE);
    }

    @Override
    protected boolean validate(Update update) {
        return update != null && update.getMessage().hasText();
    }

    @Override
    public void sendAnswer(long userId, String messageText) {
        System.out.println(new RabbitMessage(userId, messageText));
    }
}
