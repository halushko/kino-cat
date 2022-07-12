package com.halushko.torrent.parsers;

public class FilesTorrent extends ExecuteBash {
    public FilesTorrent(String id) {
        super("/home/vagrant/commands/file_list_torrent.sh", id);
    }
}
