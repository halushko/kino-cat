package com.halushko;

import com.halushko.rabKot.handlers.input.InputMessageHandler;
import com.halushko.rabKot.rabbit.RabbitMessage;
import com.halushko.rabKot.rabbit.RabbitUtils;

public class UserMessageHandler extends InputMessageHandler {
    public static final String TELEGRAM_OUTPUT_TEXT_QUEUE = System.getenv("TELEGRAM_OUTPUT_TEXT_QUEUE");
    public static final String TELEGRAM_INPUT_TEXT_QUEUE = System.getenv("TELEGRAM_INPUT_TEXT_QUEUE");

    @Override
    protected void getDeliverCallbackPrivate(RabbitMessage rabbitMessage) {
        System.out.println("Start DeliverCallbackPrivate for " + getQueue());
        try {
            String text = rabbitMessage.getText();
            long userId = rabbitMessage.getUserId();

            Command command = Scripts.getCommand(text);
            if ( command.getCommand().equals("")) {
                RabbitUtils.postMessage(rabbitMessage.getUserId(), "Command '"+ rabbitMessage.getText() + "' is not found", TELEGRAM_OUTPUT_TEXT_QUEUE);
            } else {
                RabbitUtils.postMessage(userId, command.getCommand(), command.getQueue(), command.getScript());
            }
            System.out.println("Finish DeliverCallbackPrivate for " + getQueue());
        } catch (Exception e) {
            System.out.println("During message handle got an error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    protected String getQueue() {
        return TELEGRAM_INPUT_TEXT_QUEUE;
    }
}