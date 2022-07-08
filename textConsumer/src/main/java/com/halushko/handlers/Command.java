package com.halushko.handlers;

public class Command {
    private final String fullText;
    private String parserQueue;
    private String command = "";
    private String arguments = "";
    private String script = "";

    public Command(String str) {
        this.fullText = str;
    }

    public void tryToSetScript(ScriptCollectionElement candidate) {
        if (candidate == null) return;

        String fullCommand = fullText.split(" ")[0];

        if (!fullCommand.endsWith("_")) {
            if (candidate.getCommand().equals(fullCommand)) {
                setCommandText(candidate.getCommand(), candidate.getScript());
            }
        }
        if (fullText.startsWith(candidate.getCommand()) && this.command.length() < candidate.getCommand().length()) {
            setCommandText(candidate.getCommand(), candidate.getScript());
        }
    }

    private void setCommandText(String commandText, String scriptName) {
        this.command = commandText;
        this.script = scriptName;
        this.arguments = fullText.replaceAll(this.command, "").trim();
        this.arguments = this.arguments.length() > 0 ? " " + this.arguments: "";
    }

    public String getArguments() {
        return arguments;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public String getCommand() {
        return command;
    }

    public String getParserQueue() {
        return parserQueue;
    }

    public Command setParserQueue(String parser) {
        this.parserQueue = parser;
        return this;
    }
}
