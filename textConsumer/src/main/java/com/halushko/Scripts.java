package com.halushko;

import java.util.*;

public enum Scripts {
    RESTART_MEDIA_SERVER("/restart", "pause_torrent.sh", "EXECUTE_MEDIA_COMMAND"),

    START_TORRENT_COMMAND("/start_torrent", "start_torrent.sh", "EXECUTE_TORRENT_COMMAND"),
    RESUME_TORRENT_COMMAND("/resume_", "resume_torrent.sh", "EXECUTE_TORRENT_COMMAND"),
    PAUSE_TORRENT_COMMAND("/pause_", "pause_torrent.sh", "EXECUTE_TORRENT_COMMAND"),
    SHOW_ALL_TORRENTS_LIST_COMMAND("/list", "list_torrents.sh", "EXECUTE_TORRENT_COMMAND");

    private final String command;
    private final String script;
    private final String queue;

    private static Map<String, Scripts> allCommands;

    Scripts(String command, String script, String queue) {
        this.command = command;
        this.script = script;
        this.queue = queue;
    }

    public static Command getCommand(String text) {
        if(text == null) return new Command("");

        Command tmp = new Command(text);
        for(Scripts savedCommands: getCommandList()) {
            tmp.tryToSetScript(savedCommands);
        }
        return tmp;
    }

    public String getCommand() {
        return command;
    }

    public String getScript() {
        return script;
    }

    public String getQueue() {
        return queue;
    }

    private static Collection<Scripts> getCommandList() {
        init();
        return allCommands.values();
    }

    private static void init() {
        if (allCommands == null) {
            allCommands = new LinkedHashMap<>();
            Map<String, Scripts> allCommandsTemp = new LinkedHashMap<>();
            ArrayList<Scripts> commands = new ArrayList<>(Arrays.asList(values()));
            commands.sort(Comparator.comparingInt(s -> s.command.length()));
            commands.forEach(s -> allCommandsTemp.put(s.command, s));
            allCommandsTemp.forEach((key, value) -> {
                if (!key.endsWith("_") || allCommands.keySet().stream().noneMatch(res -> res.endsWith("_") && res.startsWith(key))) {
                    allCommands.put(key, value);
                }
            });
            if (values().length != allCommandsTemp.size()) throw new RuntimeException("Commands are configured incorrectly");
        }
    }
}
