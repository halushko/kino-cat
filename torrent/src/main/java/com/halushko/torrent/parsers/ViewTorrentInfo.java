package com.halushko.torrent.parsers;

public class ViewTorrentInfo extends ExecuteBash {
    public ViewTorrentInfo(String torrentId) {
        super("/home/vagrant/commands/info_torrent.sh", torrentId);
    }
}
