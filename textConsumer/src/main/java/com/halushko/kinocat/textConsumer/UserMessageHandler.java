package com.halushko.kinocat.textConsumer;

import com.halushko.kinocat.middleware.cli.Command;
import com.halushko.kinocat.middleware.cli.Constants;
import com.halushko.kinocat.middleware.cli.ScriptsCollection;
import com.halushko.kinocat.middleware.handlers.input.InputMessageHandler;
import com.halushko.kinocat.middleware.rabbit.RabbitMessage;
import com.halushko.kinocat.middleware.rabbit.RabbitUtils;
import org.apache.log4j.Logger;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class UserMessageHandler extends InputMessageHandler {
    private static final ScriptsCollection scripts = new ScriptsCollection() {{
        addValue("/restart_media_server", "restart.sh", Constants.Queues.MediaServer.EXECUTE_MINIDLNA_COMMAND);

        addValue("/list", "list_torrents.sh", Constants.Queues.Torrent.EXECUTE_TORRENT_COMMAND_LIST);
        addValue(Constants.Commands.Torrent.LIST_TORRENT_COMMANDS, "info_torrent.sh", Constants.Queues.Torrent.EXECUTE_TORRENT_COMMAND_COMMANDS);
        addValue(Constants.Commands.Torrent.RESUME, "resume_torrent.sh", Constants.Queues.Torrent.EXECUTE_VOID_TORRENT_COMMAND);
        addValue(Constants.Commands.Torrent.PAUSE, "pause_torrent.sh", Constants.Queues.Torrent.EXECUTE_VOID_TORRENT_COMMAND);
        addValue(Constants.Commands.Torrent.TORRENT_INFO, "info_torrent.sh", Constants.Queues.Torrent.EXECUTE_TORRENT_COMMAND_INFO);
        addValue(Constants.Commands.Torrent.REMOVE_WITH_FILES, "remove_torrent.sh", Constants.Queues.Torrent.EXECUTE_VOID_TORRENT_COMMAND);
        addValue(Constants.Commands.Torrent.REMOVE_JUST_TORRENT, "remove_torrent.sh", Constants.Queues.Torrent.EXECUTE_VOID_TORRENT_COMMAND);

        addValue(Constants.Commands.Text.REMOVE_COMMAND, Constants.Commands.Text.SEND_TEXT_TO_USER, Constants.Queues.Telegram.TELEGRAM_OUTPUT_TEXT, Constants.Commands.Text.REMOVE_WARN_TEXT_FUNC);
    }};

    @Override
    protected void getDeliverCallbackPrivate(RabbitMessage rabbitMessage) {
        Logger.getRootLogger().debug("[UserMessageHandler] Start DeliverCallbackPrivate for " + getQueue());
        try {
            String text = rabbitMessage.getText();
            long userId = rabbitMessage.getUserId();
            Logger.getRootLogger().debug(String.format("[UserMessageHandler] user_id=%s, text=%s", userId, text));
            Command command = scripts.getCommand(text);
            if (command.getFinalCommand().equals("")) {
                String message = String.format("[UserMessageHandler] Command %s not found", text);
                Logger.getRootLogger().debug(message);
                RabbitUtils.postMessage(userId, message, Constants.Queues.Telegram.TELEGRAM_OUTPUT_TEXT);
            } else if (command.getFinalCommand().equals(Constants.Commands.Text.SEND_TEXT_TO_USER)) {
                List<String> additionalArguments = command.getAdditionalArguments();
                String methodName = additionalArguments.get(0);
                Logger.getRootLogger().debug(String.format("[UserMessageHandler] The text will be send by method %s to user", methodName));
                Method method = TextGenerators.class.getMethod(methodName, String.class);
                String result = (String) method.invoke(null, command.getArguments());
                RabbitUtils.postMessage(userId, result, command.getQueue());
            } else {
                Logger.getRootLogger().debug(String.format("[UserMessageHandler] Command %s found", text));
                RabbitMessage message = new RabbitMessage(userId, command.getFinalCommand());
                message.addValue("ARG", command.getArguments());
                RabbitUtils.postMessage(message, command.getQueue());
            }
            Logger.getRootLogger().debug("[UserMessageHandler] Finish DeliverCallbackPrivate for " + getQueue());
        } catch (Exception e) {
            Logger.getRootLogger().error("[UserMessageHandler] During message handle got an error: ", e);
        }
    }

    @Override
    protected String getQueue() {
        return Constants.Queues.Telegram.TELEGRAM_INPUT_TEXT;
    }
}