package com.halushko.kinocat.torrent;

import com.halushko.kinocat.torrent.requests.concrete.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world! I'm torrent!");
        org.apache.log4j.BasicConfigurator.configure();

        new DeleteOnlyTorrent().run();
        new DeleteWithFiles().run();
        new FilesList().run();
        new TorrentFullInfo().run();
        new PauseTorrent().run();
        new ResumeTorrent().run();
        new RemoveTorrentCommand().run();
        new PrintTorrentCommands().run();
        new TorrentsList().run();

        new PauseAll().run();
        new ResumeAll().run();
    }
}