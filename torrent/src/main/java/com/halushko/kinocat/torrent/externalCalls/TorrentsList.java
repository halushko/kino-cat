package com.halushko.kinocat.torrent.externalCalls;

import com.halushko.kinocat.core.cli.Constants;
import com.halushko.kinocat.core.files.ResourceReader;
import com.halushko.kinocat.core.rabbit.RabbitUtils;
import com.halushko.kinocat.core.rabbit.SmartJson;
import com.halushko.kinocat.torrent.entities.CommonTorrentEntity;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.Map;

@Slf4j
public class TorrentsList extends TransmissionWebApiExecutor {
    public TorrentsList() {
        super();
    }

    @Override
    protected void executeRequest(SmartJson message) {
        long chatId = message.getUserId();
        String requestBody = ResourceReader.readResourceContent(String.format("transmission_requests/%s", message.getText()));
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
                            sb.append(torrent.generateTorrentCommonInfo()).append("\n");
                        } else {
                            throw new RuntimeException("Can't parse torrents list");
                        }
                    });
        } else {
            sb.append(String.format("result of request is: %s", requestResult));
        }

        RabbitUtils.postMessage(chatId, sb.toString(), Constants.Queues.Telegram.TELEGRAM_OUTPUT_TEXT);
    }

    @Override
    protected String getQueue() {
        return Constants.Queues.Torrent.EXECUTE_TORRENT_COMMAND_LIST;
    }
}
