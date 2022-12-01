package com.halushko.kinocat.middleware;

import com.halushko.kinocat.middleware.cli.ExecuteBash;
import org.apache.log4j.Logger;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        for (String s: ExecuteBash.executeViaCLI("dir") ){
            Logger.getRootLogger().debug(s);
        }
    }
}