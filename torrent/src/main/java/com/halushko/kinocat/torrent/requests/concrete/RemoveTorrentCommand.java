package com.halushko.kinocat.torrent.requests.concrete;

import com.halushko.kinocat.core.Queues;
import com.halushko.kinocat.torrent.entities.TorrentEntity;
import com.halushko.kinocat.torrent.requests.common.GetTorrent;

import static com.halushko.kinocat.core.Commands.Torrent.REMOVE_JUST_TORRENT;
import static com.halushko.kinocat.core.Commands.Torrent.REMOVE_WITH_FILES;

public class RemoveTorrentCommand extends GetTorrent {
    private static final String ANSWER = "Дійсно хочете видалити торент %s?\n\n-- видалити лише сам торент: %s\n\n-- видалити ще й скачані файли: %s";

    @Override
    protected String generateAnswer(TorrentEntity torrent, String serverNumber) {
        return String.format(ANSWER
                , torrent.getName()
                , String.format("%s%s_%s", REMOVE_JUST_TORRENT, serverNumber, torrent.getId())
                , String.format("%s%s_%s", REMOVE_WITH_FILES, serverNumber, torrent.getId())
        );
    }

    @Override
    protected String getRequest() {
        return "get_torrents_names.json";
    }

    @Override
    protected String getQueue() {
        return Queues.Torrent.DELETE;
    }
}
