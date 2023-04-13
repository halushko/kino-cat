package com.halushko.kinocat.torrent.internalScripts;

import com.halushko.kinocat.core.cli.Constants;
import com.halushko.kinocat.core.cli.ScriptsCollection;
import com.halushko.kinocat.core.handlers.input.PrivateCliCommandExecutor;
import org.apache.log4j.Logger;

import java.util.List;

public class ViewTorrentInfo extends PrivateCliCommandExecutor {
    private final static ViewTorrentInfo instance = new ViewTorrentInfo();
    public static List<String> getInfo(String torrentId) {
        Logger.getRootLogger().debug("[ViewTorrentInfo] Try to find torrent info for " + torrentId);
        return instance.execute(torrentId);
    }
    @Override
    protected String getScript() {
        return Constants.Commands.Torrent.TORRENT_INFO;
    }

    @Override
    protected ScriptsCollection getScriptsCollection() {
        return TorrentScripts.scripts;
    }
}
