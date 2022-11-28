package com.halushko.rabKot.handlers.input;

import com.halushko.rabKot.rabbit.RabbitMessage;
import com.rabbitmq.client.DeliverCallback;
import org.apache.log4j.Logger;

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
                Logger.getRootLogger().debug("Start connection");
                readMessage(getQueue(), getDeliverCallback());
                Logger.getRootLogger().debug("Connected");
            } catch (Exception e) {
                Logger.getRootLogger().error("Unknown koTorrent error: ", e);
                try {
                    Thread.sleep(MEDIUM_PAUSE_MILIS);
                } catch (InterruptedException ex) {
                    Logger.getRootLogger().error("Unknown koTorrent error: ", e);
                }
        }
    }

    protected DeliverCallback getDeliverCallback() {
        return (consumerTag, delivery) -> {
            Logger.getRootLogger().debug("Get DeliverCallback for " + getQueue() + " started");
            String s = new String(delivery.getBody(), StandardCharsets.UTF_8);
            RabbitMessage message = new RabbitMessage(s);
            getDeliverCallbackPrivate(message);
        };
    }

    protected abstract void getDeliverCallbackPrivate(RabbitMessage message);

    protected abstract String getQueue();
}
