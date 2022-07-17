package com.halushko;

import com.halushko.rabKot.handlers.input.InputMessageHandler;
import com.halushko.rabKot.rabbit.RabbitMessage;
import com.halushko.rabKot.rabbit.RabbitUtils;

public class UserMessageHandler extends InputMessageHandler {
    @Override
    protected void getDeliverCallbackPrivate(RabbitMessage rabbitMessage) {
        try {
            String text = rabbitMessage.getText();
            long userId = rabbitMessage.getUserId();

            Command command = Scripts.getCommand(text);
            if ( command.getCommand().equals("")) {
                RabbitUtils.postMessage(rabbitMessage.getUserId(), "Command '"+ rabbitMessage.getText() + "' is not found", "TELEGRAM_OUTPUT_TEXT");
            } else {
                RabbitUtils.postMessage(userId, command.getCommand(), command.getQueue());
            }

//            if (text.equalsIgnoreCase("/reload_media_server")) {
////            executeViaCLI(update, "sudo systemctl restart minidlna");
//            }
//            if (text.equalsIgnoreCase("/bot_config")) {
//
//            }
//            if (text.startsWith("/commands_")) {
////                sendText(update, new PrintCommands(text.replaceAll("/commands_", "")).run());
//            }
//
//            if (text.startsWith("/remove_all_")) {
////                sendText(update, new DoNothing(text.replaceAll("/remove_all_", "")).run());
//            }
//            if (text.startsWith("/remove_")) {
////                sendText(update, new DoNothing(text.replaceAll("/remove_", "")).run());
//            }
//            if (text.startsWith("/file_list_")) {
////                sendText(update, new FilesTorrent(text.replaceAll("/file_list_", "")).run());
//            }
////            RabbitUtils.postMessage(rabbitMessage.getUserId(), rabbitMessage.getText(), "TELEGRAM_OUTPUT_TEXT");
        } catch (Exception e) {
            System.out.println("During message handle got an error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    protected String getQueue() {
        return System.getenv("TELEGRAM_INPUT_TEXT_QUEUE");
    }
}