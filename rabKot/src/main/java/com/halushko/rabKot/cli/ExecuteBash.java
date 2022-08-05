package com.halushko.rabKot.cli;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ExecuteBash {
    public static List<String> executeViaCLI(String script, String... args) {
        Process p = null;
        List<String> result = new ArrayList<>();

        String[] sss = new String[args.length + 1];
        sss[0] = "/bin/sh /home/app/" + script;
        System.arraycopy(args, 0, sss, 1, args.length);

        try {
            p = Runtime.getRuntime().exec(sss);
            try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                for (String outputLine; (outputLine = br.readLine()) != null; )
                    result.add(outputLine);
            }
        } catch (Exception e) {
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
        result.forEach(System.out::println);
        return result;
    }
}