package com.halushko.kinocat.minidlna;

@Deprecated
public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world! I'm media server!");
        org.apache.log4j.BasicConfigurator.configure();
        new MinidlnaOperator().run();
    }
}