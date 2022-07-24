package com.halushko.rabKot;

import com.halushko.rabKot.rabbit.RabbitJson;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        RabbitJson a = RabbitJson.create("Hello world!");
        System.out.println(RabbitJson.create(a.toString()));
        a.add("asd");
//        System.out.println(a);
        a.add("QWE", "qwe");
//        System.out.println(a);
//        System.out.println(RabbitJson.create(a.toString()));

        System.out.println(a.get());
        System.out.println(a.get().getParent().get("QWE"));
    }
}