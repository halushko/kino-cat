package com.halushko.kinocat.core.handlers.telegram;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@SuppressWarnings("unused")
@Slf4j
public abstract class UserMessageHandler {
    public final void readMessage(Update update) {
        if((isPing() && PingHandler.pingValidate(update)) || (!isPing() && !PingHandler.pingValidate(update) && validate(update))) {
            try {
                readMessagePrivate(update);
            } catch (IOException | TimeoutException e) {
                String text = "[UserMessageHandler] Can't read message: " + e.getMessage();
                sendAnswer(update.getMessage().getChatId(), text);
                log.error(text, e);
            }
        }
    }

    protected abstract void readMessagePrivate(Update update) throws IOException, TimeoutException;
    protected abstract boolean validate(Update update);

    protected boolean isPing() {
        return false;
    }

    public void sendAnswer(long userId, String messageText) {
        log.debug("Dummy answer: userId: {}, message: {}", userId, messageText);
    }
}
