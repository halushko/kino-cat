package com.halushko.kinocat.torrent.externalCalls;

import com.halushko.kinocat.core.cli.Constants;
import com.halushko.kinocat.core.files.ResourceReader;
import com.halushko.kinocat.core.rabbit.SmartJson;
import com.halushko.kinocat.core.rabbit.RabbitUtils;
import com.halushko.kinocat.core.web.InputMessageHandlerApiRequest;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.Map;
import java.util.stream.IntStream;

@Slf4j
// https://github.com/transmission/transmission/blob/main/docs/rpc-spec.md
public class TorrentsList extends InputMessageHandlerApiRequest {
    public TorrentsList() {
        super("http", "10.10.255.253", 9091, "transmission/rpc");
    }

    @Override
    protected void getDeliverCallbackPrivate(SmartJson message) {
        long chatId = message.getUserId();

        String requestBody = ResourceReader.readResourceContent("transmission_requests/get_torrents_list.json");
        val responce = send("", "Content-Type", "application/json");
        String sessionIdKey = "X-Transmission-Session-Id";
        String sessionIdValue = responce.getHeader(sessionIdKey);

        val responce2 = send(requestBody, "Content-Type", "application/json", sessionIdKey, sessionIdValue);
        String bodyJson = responce2.getBody();

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
                            val torrent = new SmartJson((Map<String, Object>) torrentMap);
                            String status = getStatusIcon(torrent.getValue("status"));
                            String name = torrent.getValue("name");
                            double percentDone = Double.parseDouble(torrent.getValue("percentDone"));
                            long totalSize = Long.parseLong(torrent.getValue("totalSize"));
                            String line = getLinage(percentDone);
                            String percents = getPercents(percentDone, totalSize);
                            String id = torrent.getValue("id");
                            sb.append(String.format("%s %s\n%s %s /more_%s", status, name, line, percents, id));
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

    protected String getLinage(double done) {
        int blocks = 20;
        int blackBlocks = (int) (done * blocks);
        StringBuilder line = new StringBuilder();

        IntStream.range(0, blackBlocks).mapToObj(i -> "█").forEach(line::append);
        IntStream.range(blackBlocks, blocks).mapToObj(i -> "░").forEach(line::append);

        return "||" + line + "||";
    }

    protected String getPercents(double done, long totalSize) {
        return done == 1.0
                ? " (done)"
                : " % (" + Math.round((totalSize - (long) (totalSize * done)) / 1000000.0) / 1000.0 + " Gb left)";
    }

    protected String getStatusIcon(String status) {
        return switch (status != null ? Integer.parseInt(status) : -1) {
            case 0 -> "\uD83D\uDEAB";
            case 1 -> "\uD83D\uDD51♾";
            case 2 -> "♾";
            case 3 -> "\uD83D\uDD51⬇️";
            case 4 -> "⬇️";
            case 5 -> "\uD83D\uDD51⬆️";
            case 6 -> "⬆️";
            default -> "\uD83C\uDD98";
        };
    }
}
