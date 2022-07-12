package com.halushko.torrent.parsers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ExecuteBash {
    private final String script;
    private final String[] args;

    protected ExecuteBash(String script, String... args) {
        this.script = script;
        this.args = new String[args == null ? 0 : args.length];
        System.arraycopy(args == null ? new String[0] : args, 0, this.args, 0, this.args.length);
    }

    public List<String> run() {
        return executeViaCLI(script, args);
    }

    private List<String> executeViaCLI(String script, String[] args) {
        Process p = null;
        List<String> result = new ArrayList<>();

        String[] sss = new String[args.length + 1];
        sss[0] = script;
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