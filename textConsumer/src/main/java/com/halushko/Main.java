package com.halushko;

import com.halushko.rabKot.handlers.input.InputMessageHandler;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world! I'm textConsumer!");
        UserMessageHandler handler = new UserMessageHandler();
        for(;;) {
            try {
                handler.run();
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    Thread.sleep(InputMessageHandler.LONG_PAUSE_MILIS);
                } catch (InterruptedException ignored) {
                }
            }
        }
    }
}