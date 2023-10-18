package com.halushko.kinocat.torrent.externalCalls;

import com.halushko.kinocat.core.cli.Constants;
import com.halushko.kinocat.core.files.ResourceReader;
import com.halushko.kinocat.core.handlers.input.ExternalCliCommandExecutor;
import com.halushko.kinocat.core.rabbit.RabbitUtils;
import com.halushko.kinocat.core.rabbit.SmartJson;
import com.halushko.kinocat.core.web.InputMessageHandlerApiRequest;
import com.halushko.kinocat.torrent.entities.CommonTorrentEntity;
import com.halushko.kinocat.torrent.internalScripts.ViewTorrentInfo;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class TorrentCommands extends TransmissionWebApiExecutor {
    public TorrentCommands() {
        super();
    }

    @Override
    protected void executeRequest(SmartJson message) {
        long chatId = message.getUserId();
        String torrentId = message.getValue("ARG");
        String requestBodyFormat = ResourceReader.readResourceContent(String.format("transmission_requests/%s", message.getValue("SCRIPT")));
        String requestBody = String.format(requestBodyFormat, torrentId);
        log.debug("[executeRequest] Request body:\n{}", requestBody);

        val responce = send(requestBody, "Content-Type", "application/json", sessionIdKey, sessionIdValue);
        String bodyJson = responce.getBody();
        log.debug("[executeRequest] Responce body:\n{}", bodyJson);
        val json = new SmartJson(bodyJson);

        StringBuilder sb = new StringBuilder();
        String requestResult = json.getValue("result");
        if ("success".equalsIgnoreCase(requestResult)) {
            json.getSubMessage("arguments")
                    .getSubMessage("torrents")
                    .convertToList()
                    .forEach(torrentMap -> {
                        if (torrentMap instanceof Map) {
                            //noinspection unchecked
                            val torrent = new CommonTorrentEntity((Map<String, Object>) torrentMap);
                            sb.append(torrent.generateTorrentCommands()).append("\n");
                        } else {
                            throw new RuntimeException(String.format("Can't generate torrent commands for %s", torrentId));
                        }
                    });
        } else {
            sb.append(String.format("result of request is: %s", requestResult));
        }
        RabbitUtils.postMessage(chatId, sb.toString(), Constants.Queues.Telegram.TELEGRAM_OUTPUT_TEXT);
    }

    @Override
    protected String getQueue() {
        return Constants.Queues.Torrent.EXECUTE_TORRENT_COMMAND_COMMANDS;
    }
}
