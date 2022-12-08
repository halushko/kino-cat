package com.halushko.kinocat.torrent.internalScripts;

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
        return "/torrent_";
    }

    @Override
    protected ScriptsCollection getScriptsCollection() {
        return TorrentScripts.scripts;
    }
}
