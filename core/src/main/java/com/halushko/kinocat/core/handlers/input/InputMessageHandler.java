package com.halushko.kinocat.core.handlers.input;

import com.halushko.kinocat.core.rabbit.RabbitMessage;
import com.rabbitmq.client.DeliverCallback;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

import static com.halushko.kinocat.core.rabbit.RabbitUtils.readMessage;

@Slf4j
public abstract class InputMessageHandler implements Runnable {
    public static final long LONG_PAUSE_MILIS = Long.parseLong(System.getenv("LONG_PAUSE_MILIS"));
    public static final long MEDIUM_PAUSE_MILIS = Long.parseLong(System.getenv("MEDIUM_PAUSE_MILIS"));

    @Override
    public void run() {
        String queue = getQueue();
        log.debug(String.format("[run] Start Input Message Handler. Queue: %s", queue));

        try {
            log.debug(String.format("[run] Start connection for queue: %s", queue));
            readMessage(getQueue(), getDeliverCallback());
            log.debug(String.format("[run] '%s' connected", queue));
        } catch (Exception e) {
            log.error(String.format("[run] Unknown error during connection to queue '%s'", queue), e);
            try {
                Thread.sleep(MEDIUM_PAUSE_MILIS);
            } catch (InterruptedException ex) {
                log.error(String.format("[run] InterruptedException error. Queue '%s'", queue), e);
            }
            throw new RuntimeException("exit");
        }
    }

    protected DeliverCallback getDeliverCallback() {
        return (consumerTag, delivery) -> {
            log.debug(String.format("[getDeliverCallback] Get DeliverCallback for queue '%s' started", getQueue()));
            String body = new String(delivery.getBody(), StandardCharsets.UTF_8);
            log.debug(String.format("[getDeliverCallback] body: '%s'", body));
            RabbitMessage message = new RabbitMessage(body);
            log.debug(String.format("[getDeliverCallback] RabbitMessage: '%s'", message.getRabbitMessageText()));
            getDeliverCallbackLog(message);
        };
    }

    private void getDeliverCallbackLog(RabbitMessage message) {
        log.debug(String.format("[InputMessageHandler] Start processing message=%s", message.getRabbitMessageText()));
        getDeliverCallbackPrivate(message);
        log.debug("[InputMessageHandler] Finish processing");
    }


    protected abstract void getDeliverCallbackPrivate(RabbitMessage message);

    protected abstract String getQueue();
}
