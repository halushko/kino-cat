package com.halushko.kinocat.core.cli;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class Command {
    private final String fullText;
    private String executorQueue;
    @Getter
    private String command = "";
    private String script = "";
    private String arguments = "";
    @Getter
    private String description = "";

    @Getter
    private final List<String> additionalArguments = new ArrayList<>();

    public Command(String str) {
        this.fullText = str;
    }

    public void tryToSetScript(Script candidate) {
        if (candidate == null) return;

        String fullCommand = fullText.split(" ")[0];

        if (!fullCommand.endsWith("_")) {
            if (candidate.getCommand().equals(fullCommand)) {
                setCommandText(candidate);
            }
        }
        if (fullText.startsWith(candidate.getCommand()) && this.command.length() < candidate.getCommand().length()) {
            setCommandText(candidate);
        }
    }

    private void setCommandText(Script pojo) {
        this.command = pojo.getCommand();
        this.script = pojo.getScript();
        this.arguments = fullText.replaceAll(this.command, "").trim();
        this.description = pojo.getDescription();
        this.executorQueue = pojo.getQueue();
        this.additionalArguments.clear();
        this.additionalArguments.addAll(pojo.getParams());
    }

    public String getScript() {
        return script == null || script.trim().isEmpty() ? "" : script;
    }

    public String getArguments() {
        return arguments == null || arguments.trim().isEmpty() ? "" : arguments;
    }

    public String getFinalCommand() {
        return !getScript().isEmpty() ? String.format("%s%s%s", getScript(), "".equals(getArguments()) ? "" : " ", getArguments()) : "";
    }

    public String getQueue() {
        return executorQueue;
    }
}
