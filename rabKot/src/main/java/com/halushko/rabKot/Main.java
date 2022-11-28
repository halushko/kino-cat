package com.halushko.rabKot;

import com.halushko.rabKot.cli.ExecuteBash;
import org.apache.log4j.Logger;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        for (String s: ExecuteBash.executeViaCLI("dir") ){
            Logger.getRootLogger().debug(s);
        }
    }
}