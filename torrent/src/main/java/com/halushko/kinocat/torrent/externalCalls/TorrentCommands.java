package com.halushko.kinocat.torrent.externalCalls;

import com.halushko.kinocat.middleware.cli.Constants;
import com.halushko.kinocat.middleware.handlers.input.ExternalCliCommandExecutor;
import com.halushko.kinocat.middleware.rabbit.RabbitJson;
import com.halushko.kinocat.middleware.rabbit.RabbitMessage;

import java.util.List;

public class TorrentCommands extends ExternalCliCommandExecutor {
    @Override
    protected String getResultString(List<String> lines, RabbitMessage rabbitMessage) {
        if (lines == null || lines.isEmpty()) return "Something went wrong with EXECUTE_TORRENT_COMMAND_FILE_COMMANDS";
        String arg = rabbitMessage.getValue("ARG");
        return RabbitJson.normalizedValue(
                String.format("%s\n%s%s\n%s%s\n%s%s", "<Here should be file name>",
                        Constants.Commands.Torrent.LIST_TORRENT_PAUSE, arg,
                        Constants.Commands.Torrent.LIST_TORRENT_RESUME, arg,
                        Constants.Commands.Torrent.LIST_TORRENT_INFO, arg
                )
        );
    }

    @Override
    protected String getQueue() {
        return Constants.Queues.Torrent.EXECUTE_TORRENT_COMMAND_COMMANDS;
    }
}
