package com.halushko.rabKot;

import com.halushko.rabKot.cli.ExecuteBash;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        for (String s: ExecuteBash.executeViaCLI("dir") ){
            System.out.println(s);
        }
    }
}