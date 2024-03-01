package com.halushko.kinocat.text.commands;

import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.Arrays;
import java.util.Collection;

@Slf4j
class CommandChecker {
    private final String fullText;
    private Command command = Command.getEmptyCommand();

    private CommandChecker(String str) {
        this.fullText = str;
    }

    static Command getCommand(String inputText, Collection<Command> commandsToCheck) {
        CommandChecker checker = new CommandChecker(inputText);
        commandsToCheck.forEach(checker::validate);
        return checker.command;
    }

    private void validate(Command candidate) {
        if (candidate == null) return;
        String fullCommand = fullText.split(" ")[0];
        if (!fullCommand.endsWith("_")) {
            if (candidate.getCommand().equals(fullCommand)) {
                setCommand(candidate);
            }
        }
        if (fullText.startsWith(candidate.getCommand()) && this.command.getCommand().length() < candidate.getCommand().length()) {
            setCommand(candidate);
        }
    }

    private void setCommand(Command candidate) {
        log.debug("[setCommandText] Command={}, fullText={}, Queue={}, Arguments={}", candidate.getCommand(), fullText, candidate.getQueue(), candidate.getAdditionalProperties());
        String delimiter = candidate.getCommand().endsWith("_") ? "_" : "\\s+";
        val arg = fullText.replace(candidate.getCommand(), "").trim().split(delimiter);
        command = new Command(candidate.getCommand(), candidate.getQueue(), candidate.getDescription(), candidate.getAdditionalProperties().toArray(new CommandProperties[0]));
        command.getArguments().addAll(Arrays.stream(arg).toList());
    }
}
