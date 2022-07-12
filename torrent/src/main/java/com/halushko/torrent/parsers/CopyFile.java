package com.halushko.torrent.parsers;

public class CopyFile extends ExecuteBash {
    public CopyFile(String filePath, String folder) {
        super("/home/vagrant/commands/copy_file.sh", filePath, folder);
    }
}
