package com.halushko.kinocat.core.cli;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public class Script {
    private final String command;
    private final String script;
    private final String queue;
    private final String description;
    private final List<String> params = new ArrayList<>();

    Script(String command, String description, String script, String queue, String... params) {
        this.command = command;
        this.script = script;
        this.description = description;
        this.queue = queue;
        if(params != null) {
            this.params.addAll(Arrays.asList(params));
        }
    }
}
