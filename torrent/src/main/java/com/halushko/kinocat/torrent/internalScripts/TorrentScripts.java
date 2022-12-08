package com.halushko.kinocat.torrent.internalScripts;

import com.halushko.kinocat.middleware.cli.ScriptsCollection;

class TorrentScripts {
    static final ScriptsCollection scripts = new ScriptsCollection() {{
        addValue("/list", "list_torrents.sh", "");
        addValue("/torrent_", "info_torrent.sh", "");
    }};
}
