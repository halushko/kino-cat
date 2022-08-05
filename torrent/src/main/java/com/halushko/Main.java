package com.halushko;

import com.halushko.rabKot.handlers.input.InputMessageHandler;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world! I'm torrent!");
        org.apache.log4j.BasicConfigurator.configure();
        new TorrentOperator().run();
    }
}