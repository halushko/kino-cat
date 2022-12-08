package com.halushko.kinocat.textConsumer;

import com.halushko.kinocat.middleware.cli.Command;
import com.halushko.kinocat.middleware.cli.ScriptsCollection;
import com.halushko.kinocat.middleware.handlers.input.InputMessageHandler;
import com.halushko.kinocat.middleware.rabbit.RabbitMessage;
import com.halushko.kinocat.middleware.rabbit.RabbitUtils;
import org.apache.log4j.Logger;

public class UserMessageHandler extends InputMessageHandler {
    public static final String TELEGRAM_OUTPUT_TEXT_QUEUE = System.getenv("TELEGRAM_OUTPUT_TEXT_QUEUE");
    public static final String TELEGRAM_INPUT_TEXT_QUEUE = System.getenv("TELEGRAM_INPUT_TEXT_QUEUE");

    public static final String EXECUTE_MINIDLNA_COMMAND_QUEUE = System.getenv("EXECUTE_MINIDLNA_COMMAND_QUEUE");
    public static final String EXECUTE_VOID_TORRENT_COMMAND_QUEUE = System.getenv("EXECUTE_TORRENT_COMMAND_QUEUE");

    private static final ScriptsCollection scripts = new ScriptsCollection() {{
        addValue("/restart", "restart.sh", EXECUTE_MINIDLNA_COMMAND_QUEUE);

        addValue("/start_torrent", "start_torrent.sh", EXECUTE_VOID_TORRENT_COMMAND_QUEUE);
        addValue("/resume_", "resume_torrent.sh", EXECUTE_VOID_TORRENT_COMMAND_QUEUE);
        addValue("/pause_", "pause_torrent.sh", EXECUTE_VOID_TORRENT_COMMAND_QUEUE);
        addValue("/list", "list_torrents.sh","EXECUTE_TORRENT_COMMAND_LIST");
        addValue("/torrent_", "info_torrent.sh", "EXECUTE_TORRENT_COMMAND_INFO");
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
                RabbitUtils.postMessage(rabbitMessage.getUserId(), message, TELEGRAM_OUTPUT_TEXT_QUEUE);
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
        return TELEGRAM_INPUT_TEXT_QUEUE;
    }
}