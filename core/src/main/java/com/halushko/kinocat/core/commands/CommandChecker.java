package com.halushko.kinocat.core.commands;

import com.halushko.kinocat.core.rabbit.SmartJson;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
class CommandChecker {
    private final String fullText;
    private Command command;

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
        log.debug("[setCommandText] Command={}, fullText={}, Queue={}, Arguments={}", candidate.getCommand(), fullText, candidate.getQueue(), candidate.getArguments());
        String delimiter = candidate.getCommand().endsWith("_") ? "_" : "\\s+";
        Map<String, Object> arguments = new HashMap<>() {{
            put(SmartJson.KEYS.COMMAND_ARGUMENTS.name(),
                    fullText.replace(candidate.getCommand(), "")
                            .trim()
                            .split(delimiter)
            );
        }};
        command = new Command(candidate.getCommand(), candidate.getQueue(), new SmartJson(arguments));
    }
}
