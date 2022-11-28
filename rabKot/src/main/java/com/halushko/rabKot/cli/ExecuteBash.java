package com.halushko.rabKot.cli;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ExecuteBash {
    public static List<String> executeViaCLI(String script) {
        Process p = null;
        List<String> result = new ArrayList<>();

        try {
            p = Runtime.getRuntime().exec(String.format("sh %s", "/home/app/" + script));
            //p = Runtime.getRuntime().exec("/home/app/" + script);
            try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                for (String outputLine; (outputLine = br.readLine()) != null; )
                    result.add(outputLine);
            }
            Logger.getRootLogger().debug(result);
        } catch (Exception e) {
            Logger.getRootLogger().error("Execute CLI error: ", e);
            e.printStackTrace();
        } finally {
            if (p != null) {
                try {
                    p.waitFor();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                p.destroy();
            }
        }
//        result.forEach(System.out::println);
        return result;
    }
}