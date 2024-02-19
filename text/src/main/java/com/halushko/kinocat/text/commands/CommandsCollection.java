package com.halushko.kinocat.text.commands;

import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.*;

@Slf4j
public class CommandsCollection {
    private Map<String, Command> allCommands;
    private final List<Command> values = new ArrayList<>();

    public void addValue(String command, String queue, String description, CommandProperties... additionalProperties) {
        values.add(new Command(command, queue, description, additionalProperties));
        if (Arrays.stream(additionalProperties).anyMatch(x -> x == CommandProperties.CONTAINS_SERVER_NUMBER)) {
            values.add(new Command(command + "_", queue, description, additionalProperties));
        }
    }

    public Command getCommand(String text) {
        log.debug("[getCommand] Try to get command from text [{}]", text);
        if (text == null || text.isEmpty()) return Command.getEmptyCommand();
        val result = CommandChecker.getCommand(text, getCommandList());
        log.debug("[getCommand] Command is command={}, queue={}", result.getCommand(), result.getQueue());
        return result;
    }

    @SuppressWarnings("unused")
    public Collection<Command> getCommands() {
        return new ArrayList<>(getCommandList());
    }

    private Collection<Command> getCommandList() {
        init();
        return allCommands.values();
    }

    private void init() {
        if (allCommands == null) {
            allCommands = new LinkedHashMap<>();
            Map<String, Command> allCommandsTemp = new LinkedHashMap<>();
            values.sort(Comparator.comparingInt(s -> s.getCommand().length()));
            values.forEach(s -> allCommandsTemp.put(s.getCommand(), s));
            allCommandsTemp.forEach((key, value) -> {
                if (!key.endsWith("_") || allCommands.keySet().stream().noneMatch(res -> res.endsWith("_") && res.startsWith(key))) {
                    allCommands.put(key, value);
                }
            });
            if (values.size() != allCommandsTemp.size()) throw new RuntimeException("Commands are configured incorrectly");
        }
        StringBuilder sb = new StringBuilder("All commands:\n");
        allCommands.forEach((key, value) -> sb.append(String.format("%s = %s\n", key, value)));
        log.debug(sb.toString());
    }
}
