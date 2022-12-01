package com.halushko.kinocat.middleware.handlers.telegram;

import org.telegram.telegrambots.meta.api.objects.Update;

public abstract class PingHandler extends UserMessageHandler {
    public static final String PING = System.getenv("PING");
    public static final String PONG = System.getenv("PONG");

    @Override
    protected final void readMessagePrivate(Update update) {
        sendAnswer(update.getMessage().getChatId(), PONG);
    }

    @Override
    protected final boolean validate(Update update) {
        return pingValidate(update);
    }

    static boolean pingValidate(Update update) {
        return update.getMessage().hasText() && update.getMessage().getText().equalsIgnoreCase(PING);
    }

    @Override
    protected final boolean isPing() {
        return true;
    }
}
