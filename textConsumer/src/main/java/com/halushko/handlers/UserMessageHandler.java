package com.halushko.handlers;

import com.halushko.rabKot.handlers.input.InputMessageHandler;
import com.halushko.rabKot.rabbit.RabbitMessage;
import com.halushko.rabKot.rabbit.RabbitUtils;

public class UserMessageHandler extends InputMessageHandler {
    public static final String TELEGRAM_INPUT_TEXT = "TELEGRAM_INPUT_TEXT";
    public static final String TELEGRAM_OUTPUT_TEXT = "TELEGRAM_OUTPUT_TEXT";

    @Override
    protected void getDeliverCallbackPrivate(RabbitMessage rabbitMessage) {
        try {
            String text = rabbitMessage.getText();
            long userId = rabbitMessage.getUserId();

            Command command = ScriptCollectionElement.getCommand(text);
            RabbitUtils.postMessage(userId, command.getCommand() + command.getArguments() , command.getScript());

//            if (command.is(SHOW_ALL_TORRENTS_LIST_COMMAND)) {
//                RabbitUtils.postMessage(userId, "list_torrent.sh", EXECUTE_TORRENT_COMMAND_QUEUE);
//            }
//            if (command.is(PAUSE_TORRENT_COMMAND)) {
//                RabbitUtils.postMessage(userId, "pause_torrent.sh " + command.getArguments(), EXECUTE_TORRENT_COMMAND_QUEUE);
//            }
//            if (command.is(RESUME_TORRENT_COMMAND)) {
//                RabbitUtils.postMessage(userId, "resume_torrent.sh " + command.getArguments(), EXECUTE_TORRENT_COMMAND_QUEUE);
//            }

            if (text.equalsIgnoreCase("/reload_media_server")) {
//            executeViaCLI(update, "sudo systemctl restart minidlna");
            }
            if (text.equalsIgnoreCase("/bot_config")) {

            }
            if (text.startsWith("/commands_")) {
//                sendText(update, new PrintCommands(text.replaceAll("/commands_", "")).run());
            }

            if (text.startsWith("/remove_all_")) {
//                sendText(update, new DoNothing(text.replaceAll("/remove_all_", "")).run());
            }
            if (text.startsWith("/remove_")) {
//                sendText(update, new DoNothing(text.replaceAll("/remove_", "")).run());
            }
            if (text.startsWith("/file_list_")) {
//                sendText(update, new FilesTorrent(text.replaceAll("/file_list_", "")).run());
            }
//            RabbitUtils.postMessage(rabbitMessage.getUserId(), rabbitMessage.getText(), "TELEGRAM_OUTPUT_TEXT");
        } catch (Exception e) {
            System.out.println("During message handle got an error: " + e.getMessage());
        }
    }

    @Override
    protected String getQueue() {
        return TELEGRAM_INPUT_TEXT;
    }
}