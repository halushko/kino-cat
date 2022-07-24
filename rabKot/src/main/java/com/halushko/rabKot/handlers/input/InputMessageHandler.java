package com.halushko.rabKot.handlers.input;

import com.halushko.rabKot.rabbit.RabbitMessage;
import com.rabbitmq.client.DeliverCallback;

import java.nio.charset.StandardCharsets;

import static com.halushko.rabKot.rabbit.RabbitUtils.readMessage;

public abstract class InputMessageHandler implements Runnable {
    public static final long LONG_PAUSE_MILIS;
    static {
        long str = 10000L;
        try {
            str = Long.getLong(System.getenv("PAUSE_BEFORE_START_MILIS"));
        } catch (Exception ignore) {

        }
        LONG_PAUSE_MILIS = str;
    }

    public static final long MEDIUM_PAUSE_MILIS;
    static {
        long str = 5000L;
        try {
            str = Long.getLong(System.getenv("PAUSE_AFTER_ERROR_MILIS"));
        } catch (Exception ignore) {

        }
        MEDIUM_PAUSE_MILIS = str;
    }

    public static final long SMALL_PAUSE_MILIS;
    static {
        long str = 500L;
        try {
            str = Long.getLong(System.getenv("SMALL_PAUSE_MILIS"));
        } catch (Exception ignore) {

        }
        SMALL_PAUSE_MILIS = str;
    }

    @SuppressWarnings({"InfiniteLoopStatement", "BusyWait"})
    @Override
    public void run() {
        for (; ; ) {
            try {
                Thread.sleep(LONG_PAUSE_MILIS * 2);
                handle();
            } catch (InterruptedException e) {
                System.out.println("Unknown koTorrent error: " + e.getMessage());
                try {
                    Thread.sleep(MEDIUM_PAUSE_MILIS);
                } catch (InterruptedException ex) {
                    System.out.println("Unknown koTorrent error: " + ex.getMessage());
                }
            }
        }
    }
    @SuppressWarnings({"InfiniteLoopStatement", "BusyWait"})
    protected void handle() {
        for (; ; ) {
            try {
                readMessage(getQueue(), getDeliverCallback());
            } catch (Exception e) {
                System.out.println("Unknown koTorrent error: " + e.getMessage());
                try {
                    Thread.sleep(MEDIUM_PAUSE_MILIS);
                } catch (InterruptedException ex) {
                    System.out.println("Unknown koTorrent error: " + ex.getMessage());
                }
            }
        }
    }

    protected DeliverCallback getDeliverCallback() {
        return (consumerTag, delivery) -> {
            String s = new String(delivery.getBody(), StandardCharsets.UTF_8);
            RabbitMessage message = new RabbitMessage(s);
            getDeliverCallbackPrivate(message);
        };
    }

    protected abstract void getDeliverCallbackPrivate(RabbitMessage message);

    protected abstract String getQueue();
}
