package com.halushko.kinocat.core.commands;

import com.halushko.kinocat.core.rabbit.SmartJson;
import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
public class Command {
    private final String command;
    private final String queue;
    private final String description;
    private final SmartJson arguments;

    Command(){
        command = "";
        queue = Constants.Queues.Telegram.TELEGRAM_OUTPUT_TEXT;
        arguments = new SmartJson(SmartJson.KEYS.TEXT, "Такої команди не знайдено");
        description = "нема опису";
    }

    Command(String command, String queue, String description, SmartJson arguments) {
        this.command = command == null ? "" : command;
        this.queue = queue == null ? "" : queue;
        this.description = description == null ? "нема опису" : description;
        this.arguments = arguments == null ? new SmartJson("") : arguments;
    }
    Command(String command, String queue, String description, Map<String, String> arguments) {
        this(command, queue, description, arguments == null ? new SmartJson("") : new SmartJson(new LinkedHashMap<>(arguments)));
    }
}
