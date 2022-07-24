package com.halushko.handlers.input;

import com.halushko.rabKot.handlers.input.InputMessageHandler;
import com.halushko.rabKot.rabbit.RabbitMessage;

import static com.halushko.KoTorrentBot.sendText;

public class SendTextMessageToUser extends InputMessageHandler {
    public static final String TELEGRAM_OUTPUT_TEXT_QUEUE = System.getenv("TELEGRAM_OUTPUT_TEXT_QUEUE");
    @Override
    protected void getDeliverCallbackPrivate(RabbitMessage message) {
        sendText(message.getUserId(), message.getText());
    }

    @Override
    protected String getQueue() {
        return TELEGRAM_OUTPUT_TEXT_QUEUE;
    }
}
