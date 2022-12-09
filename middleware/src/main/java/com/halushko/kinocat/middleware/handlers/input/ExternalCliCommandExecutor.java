package com.halushko.kinocat.middleware.handlers.input;

import com.halushko.kinocat.middleware.cli.ExecuteBash;
import com.halushko.kinocat.middleware.rabbit.RabbitMessage;
import com.halushko.kinocat.middleware.rabbit.RabbitUtils;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.stream.Collectors;

import static com.halushko.kinocat.middleware.rabbit.RabbitJson.normalizedValue;

@SuppressWarnings("unused")
public abstract class ExternalCliCommandExecutor extends InputMessageHandler {
    @Override
    protected void getDeliverCallbackPrivate(RabbitMessage rabbitMessage) {
        long userId = rabbitMessage.getUserId();
        String script = rabbitMessage.getText();

        Logger.getRootLogger().debug(String.format("[ExternalCliCommandExecutor] userId:%s, script:%s", userId, script));

        try {
            String textResult = getResultString(ExecuteBash.executeViaCLI(script));
            Logger.getRootLogger().debug(String.format("[ExternalCliCommandExecutor] textResult:%s, ", textResult));
            RabbitUtils.postMessage(userId, textResult, getQueue());
        } catch (Exception e) {
            Logger.getRootLogger().error("[ExternalCliCommandExecutor] Error during CLI execution: ", e);
        }
    }
    protected String getResultString(List<String> lines) {
        return lines == null || lines.isEmpty() ? "" : normalizedValue(lines.stream().map(a -> a + "\n").collect(Collectors.joining()));
    }
}
