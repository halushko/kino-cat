package com.halushko.kinocat.core.cli;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Command {
    private final String fullText;
    @Getter
    private String queue;
    @Getter
    private String command = "";
    @Getter
    private String script = "";
    @Getter
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
        log.debug("[tryToSetScript] Candidate {}", candidate.getCommand());

        String fullCommand = fullText.split(" ")[0];
        if (!fullCommand.endsWith("_")) {
            if (candidate.getCommand().equals(fullCommand)) {
                setCommandText(candidate);
            }
        }
        if (fullText.startsWith(candidate.getCommand()) && this.command.length() < candidate.getCommand().length()) {
            setCommandText(candidate);
        }
        log.debug("[tryToSetScript] Command={}, Script={}, fullText={}, Description={}, Queue={}, Arguments={}", getCommand(), getScript(), fullText, getDescription(), getQueue(), getArguments());

    }

    private void setCommandText(Script pojo) {
        log.debug("[setCommandText] Command={}, Script={}, fullText={}, Description={}, Queue={}, Arguments={}", pojo.getCommand(), pojo.getScript(), fullText, pojo.getDescription(), pojo.getQueue(), pojo.getParams());
        this.command = pojo.getCommand();
        this.script = pojo.getScript() == null || pojo.getScript().trim().isEmpty() ? "" : pojo.getScript();
        this.arguments = fullText.replaceAll(this.command, "").trim();
        this.description = pojo.getDescription();
        this.queue = pojo.getQueue();
        this.additionalArguments.clear();
        this.additionalArguments.addAll(pojo.getParams());
    }

    public String getFinalCommand() {
        return getScript().isEmpty() ?
                "" :
                String.format("%s%s%s",
                        getScript(),
                        getArguments().isEmpty() ? "" : " ",
                        getArguments()
                );
    }
}
