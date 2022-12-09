package com.halushko.kinocat.torrent.internalScripts;

import com.halushko.kinocat.middleware.cli.Constants;
import com.halushko.kinocat.middleware.cli.ScriptsCollection;

class TorrentScripts {
    static final ScriptsCollection scripts = new ScriptsCollection() {{
//        addValue("/list", "list_torrents.sh", "");
        addValue(Constants.Commands.Torrent.LIST_TORRENT_INFO, "info_torrent.sh", "NO_QUEUE");
    }};
}
