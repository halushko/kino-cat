package com.halushko;

public class Command {
    private final String fullText;
    private String parserQueue;
    private String command = "";
    private String script = "";
    private String arguments = "";

    public Command(String str) {
        this.fullText = str;
    }

    public void tryToSetScript(Scripts candidate) {
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

    private void setCommandText(Scripts pojo) {
        this.command = pojo.getCommand();
        this.script = pojo.getScript();
        this.arguments = fullText.replaceAll(this.command, "").trim();
        this.arguments = this.arguments.length() > 0 ? " " + this.arguments : "";
        this.parserQueue = pojo.getQueue();
    }

    public String getScript() {
        return script;
    }

    public String getCommand() {
        return script + " " + arguments;
    }

    public String getQueue() {
        return parserQueue;
    }

}
