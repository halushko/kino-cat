package com.halushko.kinocat.text;

import com.halushko.kinocat.text.handlers.HelpCommand;
import com.halushko.kinocat.text.handlers.UserTextMessageHandler;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world! I'm textConsumer!");
        org.apache.log4j.BasicConfigurator.configure();
        new UserTextMessageHandler().run();
        new HelpCommand().run();
    }
}