package com.halushko.kinocat.core.handlers.input;

import com.halushko.kinocat.core.Queues;
import com.halushko.kinocat.core.rabbit.RabbitUtils;
import com.halushko.kinocat.core.rabbit.SmartJson;
import com.rabbitmq.client.DeliverCallback;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

import static com.halushko.kinocat.core.rabbit.RabbitUtils.readMessage;

@Slf4j
public abstract class InputMessageHandler implements Runnable {
    public static final long LONG_PAUSE_MILIS = Long.parseLong(System.getenv("LONG_PAUSE_MILIS"));
    public static final long MEDIUM_PAUSE_MILIS = Long.parseLong(System.getenv("MEDIUM_PAUSE_MILIS"));
    public static final String OUTPUT_SEPARATOR = "#OUTPUT_SEPARATOR#";

    @Override
    public void run() {
        String queue = getQueue();
        log.debug("[run] Start Input Message Handler. Queue: {}", queue);

        try {
            log.debug("[run] Start connection for queue: {}", queue);
            readMessage(getQueue(), getDeliverCallback());
            log.debug("[run] '{}' connected", queue);
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
            log.debug("[getDeliverCallback] Get DeliverCallback for queue '{}' started", getQueue());
            String body = new String(delivery.getBody(), StandardCharsets.UTF_8);
            log.debug("[getDeliverCallback] body: '{}'", body);
            SmartJson message = new SmartJson(body);
            log.debug("[getDeliverCallback] RabbitMessage: '{}'", message.getRabbitMessageText());
            getDeliverCallbackLog(message);
        };
    }

    private void getDeliverCallbackLog(SmartJson message) {
        log.debug("[InputMessageHandler] Start processing message={}", message.getRabbitMessageText());
        if(!validate(message)) {
            log.error("[InputMessageHandler] Invalid message={}", message.getRabbitMessageText());
            RabbitUtils.postMessage(message.getUserId(), message.getRabbitMessageText(), Queues.Telegram.TELEGRAM_OUTPUT_TEXT);
        } else {
            String result = getDeliverCallbackPrivate(message);
            executePostAction(message, result);
            log.debug("[InputMessageHandler] Finish processing with result: {}", result);
        }
    }

    protected String printResult(long chatId, String text){
        String replacedString = text.replaceAll(OUTPUT_SEPARATOR + "(?=" + OUTPUT_SEPARATOR + ")", ",");
        RabbitUtils.postMessage(chatId, replacedString, Queues.Telegram.TELEGRAM_OUTPUT_TEXT);
        return replacedString;
    }

    protected boolean validate(@SuppressWarnings("unused") SmartJson message){
        return true;
    }

    @SuppressWarnings("unused")
    protected void executePostAction(SmartJson input, String output){
    }

    protected abstract String getDeliverCallbackPrivate(SmartJson message);

    protected abstract String getQueue();
}
