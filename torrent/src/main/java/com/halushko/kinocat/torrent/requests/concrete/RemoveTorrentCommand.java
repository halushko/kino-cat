package com.halushko.kinocat.torrent.requests.concrete;

import com.halushko.kinocat.core.commands.Constants;
import com.halushko.kinocat.torrent.entities.TorrentEntity;
import com.halushko.kinocat.torrent.requests.common.GetTorrent;

import static com.halushko.kinocat.core.commands.Constants.Commands.Torrent.REMOVE_JUST_TORRENT;
import static com.halushko.kinocat.core.commands.Constants.Commands.Torrent.REMOVE_WITH_FILES;

public class RemoveTorrentCommand extends GetTorrent {
    private static final String ANSWER = "Дійсно хочете видалити торент %s?\n\n-- видалити лише сам торент: %s%s\n\n-- видалити ще й скачані файли: %s%s";
    @Override
    protected String generateAnswer(TorrentEntity torrent) {
        return String.format(ANSWER, torrent.getName(), REMOVE_JUST_TORRENT, torrent.getId(), REMOVE_WITH_FILES, torrent.getId());
    }

    @Override
    protected String getRequest() {
        return "get_torrents_names.json";
    }

    @Override
    protected String getQueue() {
        return Constants.Queues.Torrent.DELETE;
    }
}
