package com.halushko.torrent.parsers;

public class PauseTorrent extends ExecuteBash {
    public PauseTorrent(String id) {
        super("/home/vagrant/commands/pause_torrent.sh", id);
    }
}
