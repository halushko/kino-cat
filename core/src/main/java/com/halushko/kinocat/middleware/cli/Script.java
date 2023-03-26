package com.halushko.kinocat.core.cli;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Script {
    private final String command;
    private final String script;
    private final String queue;
    private final List<String> params = new ArrayList<>();

    Script(String command, String script, String queue, String... params) {
        this.command = command;
        this.script = script;
        this.queue = queue;
        if(params != null) {
            this.params.addAll(Arrays.asList(params));
        }
    }

    public String getCommand() {
        return command;
    }

    public String getScript() {
        return script;
    }

    public String getQueue() {
        return queue;
    }

    public List<String> getParams() {
        return params;
    }
}
