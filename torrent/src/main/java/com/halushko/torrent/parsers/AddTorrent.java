package com.halushko.torrent.parsers;

import java.io.File;

public class AddTorrent extends ExecuteBash {
    public AddTorrent(File torrentFile) {
        super("/home/vagrant/commands/start_torrent.sh", torrentFile.getAbsolutePath());
    }

    
}
