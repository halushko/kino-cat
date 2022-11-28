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
        Logger.getRootLogger().debug("Start DeliverCallbackPrivate for " + getQueue());
        try {
            String text = rabbitMessage.getText();
            long userId = rabbitMessage.getUserId();

            Command command = Scripts.getCommand(text);
            if ( command.getCommand().equals("")) {
                RabbitUtils.postMessage(rabbitMessage.getUserId(), "Command '"+ rabbitMessage.getText() + "' is not found", TELEGRAM_OUTPUT_TEXT_QUEUE);
            } else {
                RabbitUtils.postMessage(userId, command.getCommand(), command.getQueue(), command.getScript());
            }
            Logger.getRootLogger().debug("Finish DeliverCallbackPrivate for " + getQueue());
        } catch (Exception e) {
            Logger.getRootLogger().error("During message handle got an error: ", e);
        }
    }

    @Override
    protected String getQueue() {
        return TELEGRAM_INPUT_TEXT_QUEUE;
    }
}