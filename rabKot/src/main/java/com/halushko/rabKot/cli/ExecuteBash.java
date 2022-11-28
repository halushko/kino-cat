package com.halushko.rabKot.cli;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ExecuteBash {
    public static List<String> executeViaCLI(String script) {
        String command = String.format("sh %s%s", "/home/app/", script);
        Logger.getRootLogger().debug(String.format("[executeViaCLI] Execute script: %s", command));
        Process p = null;
        List<String> result = new ArrayList<>();

        try {
            p = Runtime.getRuntime().exec(command);
            try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                for (String outputLine; (outputLine = br.readLine()) != null; )
                    result.add(outputLine);
            }
        } catch (Exception e) {
            Logger.getRootLogger().error("[executeViaCLI] Execute CLI error: ", e);
        } finally {
            if (p != null) {
                try {
                    p.waitFor();
                } catch (InterruptedException e) {
                    Logger.getRootLogger().error("[executeViaCLI] Execute CLI Wait error: ", e);
                }
                p.destroy();
            }
        }
        Logger.getRootLogger().debug("[executeViaCLI] Execution of script finished. Result is:");
        result.forEach(Logger.getRootLogger()::debug);
        return result;
    }
}