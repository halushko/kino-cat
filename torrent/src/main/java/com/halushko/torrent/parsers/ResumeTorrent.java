package com.halushko.torrent.parsers;

public class ResumeTorrent extends ExecuteBash {
    public ResumeTorrent(String id) {
        super("/home/vagrant/commands/resume_torrent.sh", id);
    }
}
