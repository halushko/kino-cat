package com.halushko;

public class Main {
    public static final String TELEGRAM_OUTPUT_TEXT_QUEUE = System.getenv("TELEGRAM_OUTPUT_TEXT_QUEUE");

    public static void main(String[] args) {
        System.out.println("Hello world! I'm fileConsumer!");
        new UserMessageHandler().run();
    }
}