package com.halushko.kinocat.core.commands;

import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.*;

@Slf4j
public class CommandsCollection {
    private Map<String, Command> allCommands;
    private final List<Command> values = new ArrayList<>();

    @SuppressWarnings("unused")
    public void addValue(String command, String queue, String description) {
        values.add(new Command(command, queue, description, new LinkedHashMap<>()));
    }
    @SuppressWarnings("unused")
    public void addValue(String command, String queue, String description, Map<String, String> params) {
        values.add(new Command(command, queue, description, params));
    }

    @SuppressWarnings("unused")
    public Command getCommand(String text) {
        log.debug("[getCommand] Try to get command from text [{}]", text);
        if (text == null || text.isEmpty()) return new Command();
        val result =  CommandChecker.getCommand(text, getCommandList());
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
