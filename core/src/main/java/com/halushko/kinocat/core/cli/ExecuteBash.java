package com.halushko.kinocat.core.cli;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ExecuteBash {
    public static List<String> executeViaCLI(String script) {
        String command = String.format("sh %s%s", "/home/app/", script);
        log.debug(String.format("[executeViaCLI] Execute script: %s", command));
        List<String> result = new ArrayList<>();

        ProcessBuilder processBuilder = new ProcessBuilder(command);
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
}