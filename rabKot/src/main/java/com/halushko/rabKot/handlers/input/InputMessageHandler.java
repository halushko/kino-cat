package com.halushko.rabKot.handlers.input;

import com.halushko.rabKot.rabbit.RabbitMessage;
import com.rabbitmq.client.DeliverCallback;
import org.apache.log4j.Logger;

import java.nio.charset.StandardCharsets;

import static com.halushko.rabKot.rabbit.RabbitUtils.readMessage;

public abstract class InputMessageHandler implements Runnable {
    public static final long LONG_PAUSE_MILIS = Long.parseLong(System.getenv("LONG_PAUSE_MILIS"));
    public static final long MEDIUM_PAUSE_MILIS = Long.parseLong(System.getenv("MEDIUM_PAUSE_MILIS"));

    @Override
    public void run() {
        String queue = getQueue();
        Logger.getRootLogger().debug(String.format("[run] Start Input Message Handler. Queue: %s", queue));
        try {
            Thread.sleep(LONG_PAUSE_MILIS*3);
        } catch (InterruptedException ignored) {
        }
            try {
                Logger.getRootLogger().debug(String.format("[run] Start connection for queue: %s", queue));
                readMessage(getQueue(), getDeliverCallback());
                Logger.getRootLogger().debug(String.format("[run] '%s' connected", queue));
            } catch (Exception e) {
                Logger.getRootLogger().error(String.format("[run] Unknown error during connection to queue '%s'", queue), e);
                try {
                    Thread.sleep(MEDIUM_PAUSE_MILIS);
                } catch (InterruptedException ex) {
                    Logger.getRootLogger().error(String.format("[run] InterruptedException error. Queue '%s'", queue), e);
                }
        }
    }

    protected DeliverCallback getDeliverCallback() {
        return (consumerTag, delivery) -> {
            Logger.getRootLogger().debug(String.format("[getDeliverCallback] Get DeliverCallback for queue '%s' started", getQueue()));
            String body = new String(delivery.getBody(), StandardCharsets.UTF_8);
            Logger.getRootLogger().debug(String.format("[getDeliverCallback] body: '%s'", body));
            RabbitMessage message = new RabbitMessage(body);
            Logger.getRootLogger().debug(String.format("[getDeliverCallback] RabbitMessage: '%s'", message));
            getDeliverCallbackLog(message);
        };
    }

    private void getDeliverCallbackLog(RabbitMessage message) {
        Logger.getRootLogger().debug(String.format("[InputMessageHandler] Start processing message=%s", message.getRabbitMessageText()));
        getDeliverCallbackPrivate(message);
        Logger.getRootLogger().debug("[InputMessageHandler] Finish processing");
    }


    protected abstract void getDeliverCallbackPrivate(RabbitMessage message);

    protected abstract String getQueue();
}
