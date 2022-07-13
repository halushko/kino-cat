package com.halushko;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world! I'm torrent!");
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