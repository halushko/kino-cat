package com.halushko.kinocat.core.cli;

import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class ScriptsCollection {
    private Map<String, Script> allCommands;
    private final List<Script> values = new ArrayList<>();

    @SuppressWarnings("unused")
    public void addValue(String command, String description, String script, String queue, String... args) {
        values.add(new Script(command, description, script, queue, args));
    }

    public Command getCommand(String text) {
        log.debug("[getCommand] Try to get command from text [{}]", text);
        if (text == null) return new Command("");

        Command tmp = new Command(text);
        getCommandList().forEach(tmp::tryToSetScript);
        log.debug("[getCommand] Command is command={}, script={}, queue={}", tmp.getFinalCommand(), tmp.getScript(), tmp.getQueue());
        return tmp;
    }

    private Collection<Script> getCommandList() {
        init();
        return allCommands.values();
    }

    private void init() {
        if (allCommands == null) {
            allCommands = new LinkedHashMap<>();
            Map<String, Script> allCommandsTemp = new LinkedHashMap<>();
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
