package com.halushko.kinocat.core.handlers.input;

import com.halushko.kinocat.core.rabbit.SmartJson;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
@Slf4j
public abstract class CliCommandExecutor extends InputMessageHandler {
    @Override
    protected String getDeliverCallbackPrivate(SmartJson rabbitMessage) {
        long userId = rabbitMessage.getUserId();
        String script = getScript(rabbitMessage);
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

    protected List<String> executeViaCLI(String script) {
//        String command = String.format("sh %s%s", "/home/app/", script);
        log.debug("[executeViaCLI] Execute script: {}", script);
        List<String> result = new ArrayList<>();

        ProcessBuilder processBuilder = new ProcessBuilder(script);
        processBuilder.redirectErrorStream(true);

        try {
            Process process = processBuilder.start();

            try (BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String outputLine;
                while ((outputLine = br.readLine()) != null) {
                    result.add(outputLine);
                }
            }

            int exitCode = process.waitFor();
            log.debug("[executeViaCLI] Execution of script finished. Exit code: {}", exitCode);
        } catch (Exception e) {
            log.error("[executeViaCLI] Execute CLI error: ", e);
        }
        log.debug("[executeViaCLI] Execution of script finished. Result is:");
        result.forEach(log::debug);
        return result;
    }

    protected String getScript(SmartJson rabbitMessage){
        return  rabbitMessage.getText();
    }
}
