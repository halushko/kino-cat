package com.halushko.kinocat.core.handlers.input;

import com.halushko.kinocat.core.rabbit.SmartJson;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
@Slf4j
public abstract class CliCommandExecutor extends InputMessageHandler {
    @Override
    protected String getDeliverCallbackPrivate(SmartJson rabbitMessage) {
        long userId = rabbitMessage.getUserId();
        val script = getScript(rabbitMessage);
        log.debug("[ExternalCliCommandExecutor] userId:{}, script:{}", userId, script);

        try {
            String textResult = getResultString(executeViaCLI(script), rabbitMessage);
            log.debug("[ExternalCliCommandExecutor] textResult: {}", textResult);
            return printResult(userId, textResult);
        } catch (Exception e) {
            String errorText = "[ExternalCliCommandExecutor] Error during CLI execution: ";
            log.error(errorText, e);
            return String.format(errorText + "{}", e.getMessage());
        }
    }

    protected String getResultString(List<String> lines, SmartJson rabbitMessage) {
        return lines == null ? "" : String.join("\n", lines);
    }

    protected List<String> executeViaCLI(String[] script) {
        log.debug("[executeViaCLI] Execute script: [{}]", String.join(", ", script));
        List<String> result = new ArrayList<>();

        try {
            Process process = Runtime.getRuntime().exec(script);
            result = new BufferedReader(new InputStreamReader(process.getInputStream()))
                    .lines()
                    .collect(Collectors.toList());
            process.waitFor();
        } catch (Exception e) {
            log.error("[executeViaCLI] Execute CLI error: ", e);
        }
        log.debug("[executeViaCLI] Execution of script finished. Result is:");
        result.forEach(log::debug);
        return result;
    }

    protected String[] getScript(SmartJson rabbitMessage) {
        return new String[]{rabbitMessage.getText()};
    }
}
