package com.halushko.rabKot.handlers.telegram;

import org.apache.log4j.Logger;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@SuppressWarnings("unused")
public abstract class UserMessageHandler {

    public final void readMessage(Update update) {
        if((isPing() && PingHandler.pingValidate(update)) || (!isPing() && !PingHandler.pingValidate(update) && validate(update))) {
            try {
                readMessagePrivate(update);
            } catch (IOException | TimeoutException e) {
                String text = "Unknown koTorrent error: " + e.getMessage();
                sendAnswer(update.getMessage().getChatId(), text);
                System.out.println(text);
                e.printStackTrace();
                Logger.getRootLogger().error(text);
                Logger.getRootLogger().error(e);
            }
        }
    }

    protected abstract void readMessagePrivate(Update update) throws IOException, TimeoutException;
    protected abstract boolean validate(Update update);

    protected boolean isPing() {
        return false;
    }

    public abstract void sendAnswer(long userId, String messageText);
}
