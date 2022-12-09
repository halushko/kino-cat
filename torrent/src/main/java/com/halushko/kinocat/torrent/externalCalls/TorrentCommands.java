package com.halushko.kinocat.torrent.externalCalls;

import com.halushko.kinocat.middleware.cli.Constants;
import com.halushko.kinocat.middleware.handlers.input.ExternalCliCommandExecutor;
import com.halushko.kinocat.middleware.rabbit.RabbitJson;

import java.util.Collections;
import java.util.List;

public class TorrentCommands extends ExternalCliCommandExecutor {
    @Override
    protected String getResultString(List<String> lines) {
        if (lines == null || lines.isEmpty()) return "Something went wrong with EXECUTE_TORRENT_COMMAND_FILE_COMMANDS";

        return RabbitJson.normalizedValue(
                String.format("%s\n%s\n%s\n%s", "<Here should be file name>",
                        Constants.Commands.Torrent.LIST_TORRENT_PAUSE,
                        Constants.Commands.Torrent.LIST_TORRENT_RESUME,
                        Constants.Commands.Torrent.LIST_TORRENT_INFO
                )
        );
    }

    @Override
    protected String getQueue() {
        return Constants.Queues.Torrent.EXECUTE_TORRENT_COMMAND_COMMANDS;
    }
}
