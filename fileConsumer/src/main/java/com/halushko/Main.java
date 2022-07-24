package com.halushko;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world! I'm fileConsumer!");
        new UserMessageHandler().run();
    }
}