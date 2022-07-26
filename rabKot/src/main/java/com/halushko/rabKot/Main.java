package com.halushko.rabKot;

import com.halushko.rabKot.handlers.input.InputMessageHandler;
import com.halushko.rabKot.rabbit.RabbitJson;
import com.halushko.rabKot.rabbit.RabbitMessage;
import com.halushko.rabKot.rabbit.RabbitUtils;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class Main {
    public static void main(String[] args) {
//        System.out.println("Hello world!");
//        RabbitJson a = RabbitJson.create("Hello world!");
//        System.out.println(RabbitJson.create(a.toString()));
//        a.add("asd");
//        System.out.println(a);
//        a.add("QWE", "qwe");
//        System.out.println(a);
//        System.out.println(RabbitJson.create(a.toString()));

//        System.out.println(a.get());
//        System.out.println(a.get().getParent().get("QWE"));

//        try {
//            RabbitUtils.postMessage(43504868, "/restart", System.getenv("TELEGRAM_INPUT_TEXT_QUEUE"));
//            RabbitUtils.readMessage(System.getenv("TELEGRAM_INPUT_TEXT_QUEUE"));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        System.out.println(RabbitJson.create("{\"USER_ID\":\"43504868\",\"TEXT\":\"5\"}"));

        String s = "{\"USER_ID\":\"43504868\",\"TEXT\":\"/restart\"}";
        RabbitMessage rm = new RabbitMessage(s);
        System.out.println(rm.getText());
    }
}