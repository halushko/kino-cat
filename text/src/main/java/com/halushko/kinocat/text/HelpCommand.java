package com.halushko.kinocat.text;

import com.halushko.kinocat.core.commands.Constants;
import com.halushko.kinocat.core.handlers.input.InputMessageHandler;
import com.halushko.kinocat.core.rabbit.SmartJson;

import java.util.stream.Collectors;

public class HelpCommand extends InputMessageHandler {
    @Override
    protected String getDeliverCallbackPrivate(SmartJson smartJson) {
        return UserTextMessageHandler.scripts.getCommands().stream()
                .map(command -> command.getCommand() + " - " + command.getDescription() + "\n")
                .collect(Collectors.joining());
    }

    @Override
    protected void executePostAction(SmartJson input, String output){
        printResult(input.getUserId(), output);
    }

    @Override
    protected String getQueue() {
        return Constants.Queues.Text.HELP;
    }
}
