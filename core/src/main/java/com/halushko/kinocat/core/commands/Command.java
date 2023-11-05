package com.halushko.kinocat.core.commands;

import com.halushko.kinocat.core.rabbit.SmartJson;
import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
public class Command {
    private final String command;
    private final String queue;
    private final SmartJson arguments;

    Command(){
        command = "";
        queue = Constants.Queues.Telegram.TELEGRAM_OUTPUT_TEXT;
        arguments = new SmartJson(SmartJson.KEYS.TEXT, "Такої команди не знайдено");
    }

    Command(String command, String queue, SmartJson arguments) {
        this.command = command == null ? "" : command;
        this.queue = queue == null ? "" : queue;
        this.arguments = arguments == null ? new SmartJson("") : arguments;
    }
    Command(String command, String queue, Map<String, String> arguments) {
        this(command, queue, arguments == null ? new SmartJson("") : new SmartJson(new LinkedHashMap<>(arguments)));
    }
}
