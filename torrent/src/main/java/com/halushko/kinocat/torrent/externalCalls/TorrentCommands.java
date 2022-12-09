package com.halushko.kinocat.torrent.externalCalls;

import com.halushko.kinocat.middleware.cli.Constants;
import com.halushko.kinocat.middleware.handlers.input.ExternalCliCommandExecutor;
import com.halushko.kinocat.middleware.rabbit.RabbitJson;
import com.halushko.kinocat.middleware.rabbit.RabbitMessage;
import com.halushko.kinocat.torrent.internalScripts.ViewTorrentInfo;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TorrentCommands extends ExternalCliCommandExecutor {
    private final static String REGEX_GET_NAME = ".*Name:\\s+(.+?)";
    private final static Pattern PATTERN_GET_NAME = Pattern.compile(REGEX_GET_NAME);

    @Override
    protected String getResultString(List<String> lines, RabbitMessage rabbitMessage) {
        if (lines == null || lines.isEmpty()) return "Something went wrong with EXECUTE_TORRENT_COMMAND_FILE_COMMANDS";
        String arg = rabbitMessage.getValue("ARG");

        Matcher matcher;
        String name = "";
        for (String info : ViewTorrentInfo.getInfo(arg)) {
            matcher = PATTERN_GET_NAME.matcher(info);
            if (matcher.find()) {
                name = matcher.group(1);
                break;
            }
        }

        return RabbitJson.normalizedValue(
                String.format("%s\n%s%s\n%s%s\n%s%s", name,
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
