package com.halushko.rabKot.handlers.input;

import com.halushko.rabKot.rabbit.RabbitMessage;
import com.rabbitmq.client.DeliverCallback;

import java.nio.charset.StandardCharsets;

import static com.halushko.rabKot.rabbit.RabbitUtils.readMessage;

public abstract class InputMessageHandler implements Runnable {
    public static final long LONG_PAUSE_MILIS = Long.parseLong(System.getenv("LONG_PAUSE_MILIS"));
    public static final long MEDIUM_PAUSE_MILIS = Long.parseLong(System.getenv("MEDIUM_PAUSE_MILIS"));
    public static final long SMALL_PAUSE_MILIS = Long.parseLong(System.getenv("SMALL_PAUSE_MILIS"));

    @Override
    public void run() {
        try {
            Thread.sleep(LONG_PAUSE_MILIS*3);
        } catch (InterruptedException ignored) {
        }

            try {
                System.out.println("Start connection");
                readMessage(getQueue(), getDeliverCallback());
                System.out.println("Connected");
            } catch (Exception e) {
                System.out.println("Unknown koTorrent error: " + e.getMessage());
                try {
                    Thread.sleep(MEDIUM_PAUSE_MILIS);
                } catch (InterruptedException ex) {
                    System.out.println("Unknown koTorrent error: " + ex.getMessage());
                }
        }
    }

    protected DeliverCallback getDeliverCallback() {
        return (consumerTag, delivery) -> {
            System.out.println("Get DeliverCallback for " + getQueue() + " started");
            String s = new String(delivery.getBody(), StandardCharsets.UTF_8);
            RabbitMessage message = new RabbitMessage(s);
            getDeliverCallbackPrivate(message);
        };
    }

    protected abstract void getDeliverCallbackPrivate(RabbitMessage message);

    protected abstract String getQueue();
}
