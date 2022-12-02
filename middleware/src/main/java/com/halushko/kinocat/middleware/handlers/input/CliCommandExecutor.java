package com.halushko.kinocat.middleware.handlers.input;

import com.halushko.kinocat.middleware.cli.ExecuteBash;
import com.halushko.kinocat.middleware.rabbit.RabbitMessage;
import com.halushko.kinocat.middleware.rabbit.RabbitUtils;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.stream.Collectors;

import static com.halushko.kinocat.middleware.rabbit.RabbitJson.normalizedValue;

@SuppressWarnings("unused")
public abstract class CliCommandExecutor extends InputMessageHandler {
    public static final String TELEGRAM_OUTPUT_TEXT_QUEUE = System.getenv("TELEGRAM_OUTPUT_TEXT_QUEUE");

    @Override
    protected void getDeliverCallbackPrivate(RabbitMessage rabbitMessage) {
        long userId = rabbitMessage.getUserId();
        String script = rabbitMessage.getText();
        String parserId = rabbitMessage.getValue(RabbitMessage.KEYS.CONSUMER);

        Logger.getRootLogger().debug(String.format("[CliCommandExecutor] userId:%s, script:%s, parserId:%s", userId, script, parserId));

        try {
            List<String> result = ExecuteBash.executeViaCLI(script);
            String textResult = normalizedValue(result.stream().map(a -> a + "\n").collect(Collectors.joining()));
            Logger.getRootLogger().debug(String.format("[CliCommandExecutor] textResult:%s, ", textResult));
            RabbitUtils.postMessage(userId, textResult, TELEGRAM_OUTPUT_TEXT_QUEUE, parserId);
        } catch (Exception e) {
            Logger.getRootLogger().error("[CliCommandExecutor] Error during CLI execution: ", e);
        }
    }
}
