package com.halushko.kinocat.text.commands;

import com.halushko.kinocat.core.Queues;
import lombok.Getter;

import java.util.*;

@Getter
public class Command {
    private final String command;
    private final String queue;
    private final String description;
    private final List<CommandProperties> additionalProperties;
    final List<String> arguments = new ArrayList<>();

    Command(String command, String queue, String description, CommandProperties... additionalProperties) {
        this.command = command == null ? "" : command;
        this.queue = queue == null ? "" : queue;
        this.description = description == null ? "нема опису" : description;
        this.additionalProperties = additionalProperties == null || additionalProperties.length == 0 ? new ArrayList<>() : Arrays.asList(additionalProperties);
    }

    public static Command getEmptyCommand() {
        return new Command(
                "",
                Queues.Telegram.TELEGRAM_OUTPUT_TEXT,
                "нема опису",
                CommandProperties.EMPTY_INSTANCE
        );
    }
}
