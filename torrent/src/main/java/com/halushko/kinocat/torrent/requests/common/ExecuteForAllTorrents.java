package com.halushko.kinocat.torrent.requests.common;

import com.halushko.kinocat.core.rabbit.RabbitUtils;
import com.halushko.kinocat.core.rabbit.SmartJson;

import java.util.ArrayList;
import java.util.List;

public abstract class ExecuteForAllTorrents extends VoidTorrentOperator {
    @Override
    protected final List<String> executeRequest(SmartJson message) {
        return new ArrayList<>();
    }

    @Override
    protected final String getCommandNameForOutputText() {
        return "";
    }

    @Override
    protected void executePostAction(SmartJson input, String output) {
        String newArguments = output.replace(OUTPUT_SEPARATOR, ",");
        RabbitUtils.postMessage(
                input.getUserId(),
                input.addValue(SmartJson.KEYS.COMMAND_ARGUMENTS, newArguments).getRabbitMessageText(),
                getQueueForPostAction()
        );
    }

    @Override
    protected final String printResult(long chatId, String text) {
        return text;
    }

    @Override
    protected final String getRequest() {
        return "get_all_ids.json";
    }

    protected abstract String getQueueForPostAction();
}