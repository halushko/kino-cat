package com.halushko.kinocat.torrent.consumers;

import com.halushko.kinocat.middleware.handlers.input.ExternalCliCommandExecutor;
import com.halushko.kinocat.torrent.entities.ActiveTorrentEntity;

import java.util.List;
import java.util.stream.Collectors;

public class TorrentsList extends ExternalCliCommandExecutor {

    @Override
    protected String getResultString(List<String> lines) {
        if (lines == null || lines.isEmpty()) return "";

        return super.getResultString(lines.stream()
                .map(ActiveTorrentEntity::new)
                .filter(a -> !"-1".equals(a.id))
                .sorted((o1, o2) -> o1.status == null ? -1 : o1.status.compareToIgnoreCase(o2.status))
                .map(a -> String.format("%s %s\n%s /commands_%s", a.getStatusIcon(), a.name, a.getPercents(), a.id))
                .collect(Collectors.toList())
        );
    }

    @Override
    protected String getQueue() {
        return "EXECUTE_TORRENT_COMMAND_LIST";
    }
}
