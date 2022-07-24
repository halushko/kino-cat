package com.halushko.rabKot.handlers.telegram;

import org.telegram.telegrambots.meta.api.objects.Update;

public abstract class PingHandler extends UserMessageHandler {
    public static final String PING;
    static {
        String str = "/ping";
        try {
            str = System.getenv("PING");
        } catch (Exception ignore) {
        }
        PING = str;
    }

    public static final String PONG;
    static {
        String str = "pong";
        try {
            str = System.getenv("PONG");
        } catch (Exception ignore) {

        }
        PONG = str;
    }

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
