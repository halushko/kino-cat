package com.halushko.kinocat.torrent.externalCalls;

import com.halushko.kinocat.core.cli.Constants;
import com.halushko.kinocat.core.files.ResourceReader;
import com.halushko.kinocat.core.handlers.input.ExternalCliCommandExecutor;
import com.halushko.kinocat.core.rabbit.RabbitUtils;
import com.halushko.kinocat.core.rabbit.SmartJson;
import com.halushko.kinocat.core.web.InputMessageHandlerApiRequest;
import com.halushko.kinocat.torrent.entities.CommonTorrentEntity;
import com.halushko.kinocat.torrent.internalScripts.ViewTorrentInfo;
import lombok.val;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TorrentCommands extends TransmissionWebApiExecutor {
    public TorrentCommands() {
        super();
    }
//    @Override
//    protected String getResultString(List<String> lines, SmartJson rabbitMessage) {
//        if (lines == null || lines.isEmpty()) return "Something went wrong with EXECUTE_TORRENT_COMMAND_FILE_COMMANDS";
//        String arg = rabbitMessage.getValue("ARG");
//
//        Matcher matcher;
//        String name = "";
//        for (String info : ViewTorrentInfo.getInfo(arg)) {
//            matcher = PATTERN_GET_NAME.matcher(info);
//            if (matcher.find()) {
//                name = matcher.group(1);
//                break;
//            }
//        }
//
//        return String.format("%s\n%s%s\n%s%s\n%s%s\n%s%s", name,
//                Constants.Commands.Torrent.PAUSE, arg,
//                Constants.Commands.Torrent.RESUME, arg,
//                Constants.Commands.Torrent.TORRENT_INFO, arg,
//                Constants.Commands.Text.REMOVE_COMMAND, arg
//        );
//    }

    @Override
    protected void executeRequest(SmartJson message) {
        long chatId = message.getUserId();
        String requestBodyFormat = ResourceReader.readResourceContent(String.format("transmission_requests/%s", message.getText()));
        String torrentId = message.getValue("ARG");
        String requestBody = String.format(requestBodyFormat, torrentId);

        val responce = send(requestBody, "Content-Type", "application/json", sessionIdKey, sessionIdValue);
        String bodyJson = responce.getBody();
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
