package com.halushko.kinocat.core.handlers.input;

import com.halushko.kinocat.core.cli.Constants;
import com.halushko.kinocat.core.cli.ExecuteBash;
import com.halushko.kinocat.core.rabbit.RabbitJson;
import com.halushko.kinocat.core.rabbit.RabbitMessage;
import com.halushko.kinocat.core.rabbit.RabbitUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

import static com.halushko.kinocat.core.rabbit.RabbitJson.normalizedValue;

@SuppressWarnings("unused")
@Slf4j
public abstract class ExternalCliCommandExecutor extends InputMessageHandler {
    @Override
    protected void getDeliverCallbackPrivate(RabbitMessage rabbitMessage) {
        long userId = rabbitMessage.getUserId();
        String script = RabbitJson.unNormalizeText(rabbitMessage.getText());

        log.debug(String.format("[ExternalCliCommandExecutor] userId:%s, script:%s", userId, script));

        try {
            String textResult = getResultString(ExecuteBash.executeViaCLI(script), rabbitMessage);
            log.debug(String.format("[ExternalCliCommandExecutor] textResult:%s", textResult));
            RabbitUtils.postMessage(userId, textResult, Constants.Queues.Telegram.TELEGRAM_OUTPUT_TEXT);
        } catch (Exception e) {
            log.error("[ExternalCliCommandExecutor] Error during CLI execution: ", e);
        }
    }
    protected String getResultString(List<String> lines, RabbitMessage rabbitMessage) {
        return lines == null || lines.isEmpty() ? "" : normalizedValue(lines.stream().map(a -> a + "\n").collect(Collectors.joining()));
    }
}
