package com.halushko.kinocat.torrent.externalCalls;

import com.halushko.kinocat.middleware.cli.Constants;
import com.halushko.kinocat.middleware.handlers.input.ExternalCliCommandExecutor;
import com.halushko.kinocat.middleware.rabbit.RabbitMessage;
import com.halushko.kinocat.torrent.entities.ActiveTorrentEntity;

import java.util.List;
import java.util.stream.Collectors;

public class TorrentsList extends ExternalCliCommandExecutor {
    @Override
    protected String getResultString(List<String> lines, RabbitMessage rabbitMessage) {
        if (lines == null || lines.isEmpty()) return "";

        return super.getResultString(lines.stream()
                        .map(ActiveTorrentEntity::new)
                        .filter(a -> !"-1".equals(a.id))
                        .sorted((o1, o2) -> o1.status == null ? -1 : o1.status.compareToIgnoreCase(o2.status))
                        .map(a -> String.format("%s %s\n%s %s%s", a.getStatusIcon(), a.name, a.getPercents(), Constants.Commands.Torrent.LIST_TORRENT_COMMANDS, a.id))
                        .collect(Collectors.toList()),
                rabbitMessage
        );
    }

    @Override
    protected String getQueue() {
        return Constants.Queues.Torrent.EXECUTE_TORRENT_COMMAND_LIST;
    }
}
