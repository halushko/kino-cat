package com.halushko;

import com.halushko.rabKot.handlers.input.InputMessageHandler;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world! I'm torrent!");
        TorrentOperator handler = new TorrentOperator();
        for(;;) {
            try {
                Thread.sleep(InputMessageHandler.LONG_PAUSE_MILIS);
                handler.run();
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    Thread.sleep(InputMessageHandler.MEDIUM_PAUSE_MILIS);
                } catch (InterruptedException ignored) {
                }
            }
        }
    }
}