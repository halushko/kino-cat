package com.halushko.kinocat.text.handlers;

import com.halushko.kinocat.core.Queues;
import com.halushko.kinocat.core.handlers.input.InputMessageHandler;
import com.halushko.kinocat.core.rabbit.SmartJson;
import com.halushko.kinocat.text.Constants;

import java.util.stream.Collectors;

public class HelpCommand extends InputMessageHandler {
    @Override
    protected String getDeliverCallbackPrivate(SmartJson smartJson) {
        return Constants.COMMANDS_COLLECTION.getCommands().stream()
                .map(command -> command.getCommand() + " - " + command.getDescription() + "\n")
                .collect(Collectors.joining());
    }

    @Override
    protected void executePostAction(SmartJson input, String output){
        printResult(input.getUserId(), output);
    }

    @Override
    protected String getQueue() {
        return Queues.Text.HELP;
    }
}
