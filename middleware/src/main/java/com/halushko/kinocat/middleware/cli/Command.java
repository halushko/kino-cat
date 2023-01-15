package com.halushko.kinocat.middleware.cli;

import java.util.ArrayList;
import java.util.List;

public class Command {
    private final String fullText;
    private String executorQueue;
    private String command = "";
    private String script = "";
    private String arguments = "";

    private List<String> additionalArguments = new ArrayList<>();

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
//        this.arguments = this.arguments.length() > 0 ? " " + this.arguments : "";
        this.executorQueue = pojo.getQueue();
        this.additionalArguments.clear();
        this.additionalArguments.addAll(pojo.getParams());
    }

    public String getScript() {
        return script == null || script.trim().equals("") ? "" : script;
    }

    public String getArguments() {
        return arguments == null || arguments.trim().equals("") ? "" : arguments;
    }

    public String getFinalCommand() {
        if (getScript().equals("") ) {
            return "";
        } else {
            return String.format("%s%s%s", getScript(), "".equals(getArguments()) ? "" : " ", getArguments());
        }
    }

    public String getQueue() {
        return executorQueue;
    }

    public List<String> getAdditionalArguments() {
        return additionalArguments;
    }

    public String getCommand() {
        return command;
    }
}
