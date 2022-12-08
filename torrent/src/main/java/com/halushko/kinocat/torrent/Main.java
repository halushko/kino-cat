package com.halushko.kinocat.torrent;

import com.halushko.kinocat.torrent.consumers.VoidTorrentOperator;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world! I'm torrent!");
        org.apache.log4j.BasicConfigurator.configure();
        new VoidTorrentOperator().run();
    }
}