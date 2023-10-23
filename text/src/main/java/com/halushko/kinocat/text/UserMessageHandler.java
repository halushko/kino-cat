package com.halushko.kinocat.text;

import com.halushko.kinocat.core.cli.Command;
import com.halushko.kinocat.core.cli.Constants;
import com.halushko.kinocat.core.cli.ScriptsCollection;
import com.halushko.kinocat.core.handlers.input.InputMessageHandler;
import com.halushko.kinocat.core.rabbit.SmartJson;
import com.halushko.kinocat.core.rabbit.RabbitUtils;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.List;

@Slf4j
public class UserMessageHandler extends InputMessageHandler {
    private static final ScriptsCollection scripts = new ScriptsCollection() {{
        addValue(Constants.Commands.Torrent.LIST_TORRENTS, "", "get_torrents_list.json", Constants.Queues.Torrent.EXECUTE_TORRENT_COMMAND_LIST);
        addValue(Constants.Commands.Torrent.LIST_TORRENT_COMMANDS, "", "get_torrents_names.json", Constants.Queues.Torrent.EXECUTE_TORRENT_COMMAND_COMMANDS);
        addValue(Constants.Commands.Torrent.LIST_FILES, "", "file_list.json", Constants.Queues.Torrent.EXECUTE_TORRENT_COMMAND_LIST_FILES);
        addValue(Constants.Commands.Torrent.RESUME, "resume download", "resume_torrent.json", Constants.Queues.Torrent.EXECUTE_VOID_TORRENT_COMMAND);
        addValue(Constants.Commands.Torrent.PAUSE, "pause download", "pause_torrent.json", Constants.Queues.Torrent.EXECUTE_VOID_TORRENT_COMMAND);
        addValue(Constants.Commands.Torrent.TORRENT_INFO, "", "info_torrent.sh", Constants.Queues.Torrent.EXECUTE_TORRENT_COMMAND_INFO);
        addValue(Constants.Commands.Torrent.REMOVE_WITH_FILES, "remove torrent and related files", "remove_with_files.json", Constants.Queues.Torrent.EXECUTE_VOID_TORRENT_COMMAND);
        addValue(Constants.Commands.Torrent.REMOVE_JUST_TORRENT, "remove torrent but leave files", "remove_only_torrent.json", Constants.Queues.Torrent.EXECUTE_VOID_TORRENT_COMMAND);
        addValue(Constants.Commands.Text.REMOVE_COMMAND, "", Constants.Commands.Text.SEND_TEXT_TO_USER, Constants.Queues.Telegram.TELEGRAM_OUTPUT_TEXT, Constants.Commands.Text.REMOVE_WARN_TEXT_FUNC);
    }};

    @Override
    protected void getDeliverCallbackPrivate(SmartJson rabbitMessage) {
        log.debug("[UserMessageHandler] Start DeliverCallbackPrivate for " + getQueue());
        try {
            String text = rabbitMessage.getText();
            long userId = rabbitMessage.getUserId();
            log.debug("[UserMessageHandler] user_id={} text={}", userId, text);
            Command command = scripts.getCommand(text);
            String finalCommand = command.getFinalCommand();
            log.debug("[UserMessageHandler] Command: [getFinalCommand={}]", finalCommand);
            if (finalCommand == null || finalCommand.isEmpty()) {
                String message = String.format("[UserMessageHandler] Command %s not found", text);
                log.debug(message);
                RabbitUtils.postMessage(userId, message, Constants.Queues.Telegram.TELEGRAM_OUTPUT_TEXT);
            } else if (command.getScript().equals(Constants.Commands.Text.SEND_TEXT_TO_USER)) {
                List<String> additionalArguments = command.getAdditionalArguments();
                String methodName = additionalArguments.get(0);
                log.debug("[UserMessageHandler] The text will be send by method {} to user", methodName);
                Method method = TextGenerators.class.getMethod(methodName, String.class);
                String result = (String) method.invoke(null, command.getArguments());
                RabbitUtils.postMessage(userId, result, command.getQueue());
            } else {
                log.debug("[UserMessageHandler] Command {} found", text);
                SmartJson message = new SmartJson(userId, command.getFinalCommand());
                message.addValue("ARG", command.getArguments());
                message.addValue("SCRIPT", command.getScript());
                message.addValue("DESCRIPTION", command.getDescription());
                RabbitUtils.postMessage(message, command.getQueue());
            }
            log.debug("[UserMessageHandler] Finish DeliverCallbackPrivate for {}", getQueue());
        } catch (Exception e) {
            log.error("[UserMessageHandler] During message handle got an error: ", e);
        }
    }

    @Override
    protected String getQueue() {
        return Constants.Queues.Telegram.TELEGRAM_INPUT_TEXT;
    }
}