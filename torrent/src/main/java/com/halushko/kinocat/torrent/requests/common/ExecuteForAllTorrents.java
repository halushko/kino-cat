package com.halushko.kinocat.torrent.requests.common;

import com.halushko.kinocat.core.rabbit.RabbitUtils;
import com.halushko.kinocat.core.rabbit.SmartJson;
import com.halushko.kinocat.torrent.entities.TorrentEntity;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public abstract class ExecuteForAllTorrents extends GetTorrent {
    @Override
    protected void executePostAction(SmartJson input, String output) {
        List<String> newArguments = new ArrayList<>() {{
            add(output.replace(OUTPUT_SEPARATOR, ",").replaceAll(",+", ",").replaceAll(",+$", ""));
        }};
        SmartJson newInput = input.addValue(SmartJson.KEYS.COMMAND_ARGUMENTS, newArguments);
        log.debug("[executePostAction] Start to execute a post action. input:\n{}\noutput:{}\nqueue:{}", newInput, output, getQueueForPostAction());
        RabbitUtils.postMessage(newInput, getQueueForPostAction());
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

    @Override
    protected final String generateAnswer(TorrentEntity torrent) {
        return torrent.getId();
    }
}