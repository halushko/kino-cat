package com.halushko.kinocat.middleware.cli;

public class Script {
    private final String command;
    private final String script;
    private final String queue;

    Script(String command, String script, String queue) {
        this.command = command;
        this.script = script;
        this.queue = queue;
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
}
