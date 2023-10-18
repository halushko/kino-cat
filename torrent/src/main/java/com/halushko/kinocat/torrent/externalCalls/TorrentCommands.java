package com.halushko.kinocat.torrent.externalCalls;

import com.halushko.kinocat.core.cli.Constants;
import com.halushko.kinocat.core.handlers.input.ExternalCliCommandExecutor;
import com.halushko.kinocat.core.rabbit.SmartJson;
import com.halushko.kinocat.torrent.internalScripts.ViewTorrentInfo;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TorrentCommands extends ExternalCliCommandExecutor {
    private final static String REGEX_GET_NAME = ".*Name:\\s+(.+?)\n.*$";
    private final static Pattern PATTERN_GET_NAME = Pattern.compile(REGEX_GET_NAME);

    @Override
    protected String getResultString(List<String> lines, SmartJson rabbitMessage) {
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

        return String.format("%s\n%s%s\n%s%s\n%s%s\n%s%s", name,
                Constants.Commands.Torrent.PAUSE, arg,
                Constants.Commands.Torrent.RESUME, arg,
                Constants.Commands.Torrent.TORRENT_INFO, arg,
                Constants.Commands.Text.REMOVE_COMMAND, arg
        );
    }

    @Override
    protected String getQueue() {
        return Constants.Queues.Torrent.EXECUTE_TORRENT_COMMAND_COMMANDS;
    }
}
