package com.halushko.kinocat.textConsumer;

import com.halushko.kinocat.middleware.cli.Command;
import com.halushko.kinocat.middleware.cli.Constants;
import com.halushko.kinocat.middleware.cli.ScriptsCollection;
import com.halushko.kinocat.middleware.handlers.input.InputMessageHandler;
import com.halushko.kinocat.middleware.rabbit.RabbitMessage;
import com.halushko.kinocat.middleware.rabbit.RabbitUtils;
import org.apache.log4j.Logger;

public class UserMessageHandler extends InputMessageHandler {
    private static final ScriptsCollection scripts = new ScriptsCollection() {{
        addValue("/restart_media_server", "restart.sh", Constants.Queues.MediaServer.EXECUTE_MINIDLNA_COMMAND);

        addValue(Constants.Commands.Torrent.START_TORRENT_FILE, "start_torrent.sh", Constants.Queues.Torrent.EXECUTE_VOID_TORRENT_COMMAND);
        addValue("/list", "list_torrents.sh", Constants.Queues.Torrent.EXECUTE_TORRENT_COMMAND_LIST);
        addValue(Constants.Commands.Torrent.LIST_TORRENT_COMMANDS, "info_torrent.sh", Constants.Queues.Torrent.EXECUTE_TORRENT_COMMAND_COMMANDS);
        addValue(Constants.Commands.Torrent.LIST_TORRENT_RESUME, "resume_torrent.sh", Constants.Queues.Torrent.EXECUTE_VOID_TORRENT_COMMAND);
        addValue(Constants.Commands.Torrent.LIST_TORRENT_PAUSE, "pause_torrent.sh", Constants.Queues.Torrent.EXECUTE_VOID_TORRENT_COMMAND);
        addValue(Constants.Commands.Torrent.LIST_TORRENT_INFO, "info_torrent.sh", Constants.Queues.Torrent.EXECUTE_TORRENT_COMMAND_INFO);
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
                RabbitUtils.postMessage(rabbitMessage.getUserId(), message, Constants.Queues.Telegram.TELEGRAM_OUTPUT_TEXT);
            } else {
                Logger.getRootLogger().debug(String.format("[UserMessageHandler] Command %s found", text));
                RabbitUtils.postMessage(userId, command.getFinalCommand(), command.getQueue());
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