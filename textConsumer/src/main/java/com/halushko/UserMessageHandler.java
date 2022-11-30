package com.halushko;

import com.halushko.rabKot.handlers.input.InputMessageHandler;
import com.halushko.rabKot.rabbit.RabbitMessage;
import com.halushko.rabKot.rabbit.RabbitUtils;
import org.apache.log4j.Logger;

public class UserMessageHandler extends InputMessageHandler {
    public static final String TELEGRAM_OUTPUT_TEXT_QUEUE = System.getenv("TELEGRAM_OUTPUT_TEXT_QUEUE");
    public static final String TELEGRAM_INPUT_TEXT_QUEUE = System.getenv("TELEGRAM_INPUT_TEXT_QUEUE");

    @Override
    protected void getDeliverCallbackPrivate(RabbitMessage rabbitMessage) {
        Logger.getRootLogger().debug("[UserMessageHandler] Start DeliverCallbackPrivate for " + getQueue());
        try {
            String text = rabbitMessage.getText();
            long userId = rabbitMessage.getUserId();
            Logger.getRootLogger().debug(String.format("[UserMessageHandler] user_id=%s, text=%s", userId, text));
            Command command = Scripts.getCommand(text);
            if ( command.getCommand().equals("")) {
                String message = String.format("[UserMessageHandler] Command %s not found", text);
                Logger.getRootLogger().debug(message);
                RabbitUtils.postMessage(rabbitMessage.getUserId(), message, TELEGRAM_OUTPUT_TEXT_QUEUE);
            } else {
                Logger.getRootLogger().debug(String.format("[UserMessageHandler] Command %s found", text));
                RabbitUtils.postMessage(userId, command.getCommand(), command.getQueue(), command.getScript());
            }
            Logger.getRootLogger().debug("[UserMessageHandler] Finish DeliverCallbackPrivate for " + getQueue());
        } catch (Exception e) {
            Logger.getRootLogger().error("[UserMessageHandler] During message handle got an error: ", e);
        }
    }

    @Override
    protected String getQueue() {
        return TELEGRAM_INPUT_TEXT_QUEUE;
    }
}