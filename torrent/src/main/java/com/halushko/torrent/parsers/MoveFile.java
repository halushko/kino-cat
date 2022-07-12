package com.halushko.torrent.parsers;

import java.io.File;

public class MoveFile extends ExecuteBash {
    public MoveFile(File file, String folder) {
        super("/home/vagrant/commands/move_file.sh", file.getAbsolutePath(), folder);
    }
}
