package com.halushko.kinocat.torrent.internalScripts;

import com.halushko.kinocat.middleware.cli.Constants;
import com.halushko.kinocat.middleware.cli.ScriptsCollection;
import com.halushko.kinocat.middleware.handlers.input.PrivateCliCommandExecutor;

import java.util.List;

public class ViewTorrentInfo extends PrivateCliCommandExecutor {
    private final static ViewTorrentInfo instance = new ViewTorrentInfo();
    public static List<String> getInfo(String torrentId) {
        return instance.execute(torrentId);
    }
    @Override
    protected String getScript() {
        return Constants.Commands.Torrent.LIST_TORRENT_INFO;
    }

    @Override
    protected ScriptsCollection getScriptsCollection() {
        return TorrentScripts.scripts;
    }
}
