package com.halushko;

import com.halushko.torrent.TorrentOperator;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world! I'm torrent!");
//        System.out.println(ScriptCollectionElement.getCommand("/pause_1").getScript());
//        System.out.println(ScriptCollectionElement.getCommand("/pause").getScript());
//        System.out.println(ScriptCollectionElement.getCommand("/pause_1_2").getScript());
        TorrentOperator handler = new TorrentOperator();
        for(;;) {
            try {
                handler.run();
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ignored) {
                }
            }
        }
    }
}