package com.halushko.kinocat.text;

import com.halushko.kinocat.core.commands.Command;
import com.halushko.kinocat.core.commands.CommandsCollection;
import com.halushko.kinocat.core.commands.Constants;
import com.halushko.kinocat.core.handlers.input.InputMessageHandler;
import com.halushko.kinocat.core.rabbit.RabbitUtils;
import com.halushko.kinocat.core.rabbit.SmartJson;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserMessageHandler extends InputMessageHandler {
    private static final CommandsCollection scripts = new CommandsCollection() {{
        addValue(Constants.Commands.Torrent.LIST_TORRENTS, Constants.Queues.Torrent.TORRENTS_LIST);
        addValue(Constants.Commands.Torrent.LIST_TORRENT_COMMANDS, Constants.Queues.Torrent.TORRENT_COMMANDS);
        addValue(Constants.Commands.Torrent.LIST_FILES, Constants.Queues.Torrent.FILES_LIST);
        addValue(Constants.Commands.Torrent.RESUME, Constants.Queues.Torrent.RESUME_TORRENT);
        addValue(Constants.Commands.Torrent.PAUSE, Constants.Queues.Torrent.PAUSE_TORRENT);
        addValue(Constants.Commands.Torrent.RESUME_ALL, Constants.Queues.Torrent.RESUME_ALL);
        addValue(Constants.Commands.Torrent.PAUSE_ALL, Constants.Queues.Torrent.PAUSE_ALL);
//        addValue(Torrent.TORRENT_INFO, "", "info_torrent.sh", Constants.Queues.Torrent.TORRENT_INFO);
        addValue(Constants.Commands.Torrent.REMOVE_WITH_FILES, Constants.Queues.Torrent.DELETE_WITH_FILES);
        addValue(Constants.Commands.Torrent.REMOVE_JUST_TORRENT, Constants.Queues.Torrent.DELETE_ONLY_TORRENT);
        addValue(Constants.Commands.Torrent.REMOVE_COMMAND, Constants.Queues.Torrent.DELETE);

        addValue(Constants.Commands.Text.HELP, Constants.Queues.Torrent.RESUME_ALL);
    }};

    @Override
    protected String getDeliverCallbackPrivate(SmartJson rabbitMessage) {
        log.debug("[UserMessageHandler] Start DeliverCallbackPrivate for " + getQueue());
        try {
            String text = rabbitMessage.getText();
            long userId = rabbitMessage.getUserId();
            log.debug("[UserMessageHandler] user_id={} text={}", userId, text);
            Command command = scripts.getCommand(text);
            log.debug("[UserMessageHandler] Command: queue={}, arguments={}", command.getQueue(), command.getArguments().getRabbitMessageText());
            RabbitUtils.postMessage(command.getArguments().addValue(SmartJson.KEYS.USER_ID, String.valueOf(userId)), command.getQueue());
            log.debug("[UserMessageHandler] Finish DeliverCallbackPrivate for {}", getQueue());
        } catch (Exception e) {
            log.error("[UserMessageHandler] During message handle got an error: ", e);
        }
        return "";
    }

    @Override
    protected String getQueue() {
        return Constants.Queues.Telegram.TELEGRAM_INPUT_TEXT;
    }
}