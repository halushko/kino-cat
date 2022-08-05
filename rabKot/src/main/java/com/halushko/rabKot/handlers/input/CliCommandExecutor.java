package com.halushko.rabKot.handlers.input;

import com.halushko.rabKot.cli.ExecuteBash;
import com.halushko.rabKot.rabbit.RabbitMessage;
import com.halushko.rabKot.rabbit.RabbitUtils;

import java.util.List;
import java.util.stream.Collectors;

public abstract class CliCommandExecutor extends InputMessageHandler {
    public static final String TELEGRAM_OUTPUT_TEXT_QUEUE = System.getenv("TELEGRAM_OUTPUT_TEXT_QUEUE");

    @Override
    protected void getDeliverCallbackPrivate(RabbitMessage rabbitMessage) {
        long userId = rabbitMessage.getUserId();
        String script = rabbitMessage.getText();

        try {
            List<String> result = ExecuteBash.executeViaCLI(script);
            String textResult = result.stream().map(a -> a + "\n").collect(Collectors.joining());
            RabbitUtils.postMessage(userId, textResult, TELEGRAM_OUTPUT_TEXT_QUEUE, getParserId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public abstract String getParserId();
}
