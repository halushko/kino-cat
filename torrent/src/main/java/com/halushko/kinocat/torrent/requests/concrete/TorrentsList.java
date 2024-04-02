package com.halushko.kinocat.torrent.requests.concrete;

import com.halushko.kinocat.core.Commands;
import com.halushko.kinocat.core.Queues;
import com.halushko.kinocat.torrent.entities.TorrentEntity;
import com.halushko.kinocat.torrent.requests.common.GetTorrent;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.IntStream;

@Slf4j
public class TorrentsList extends GetTorrent {
    private final static String SYMBOLS_TO_IGNORE = "[\\s._\"']";

    public TorrentsList() {
        super();
    }

    @Override
    protected String generateAnswer(TorrentEntity torrent, String serverNumber, String serverVsTorrentSeparator) {
        return String.format("%s %s\n%s %s\n%s %s"
                , getStatusIcon(torrent), torrent.getName()
                , getProgressBar(torrent), getGigabytesLeft(torrent)
                , String.format("%s%s%s%s", Commands.Torrent.LIST_TORRENT_COMMANDS, serverNumber, serverVsTorrentSeparator, torrent.getId())
                , String.format("%s%s%s%s", Commands.Torrent.LIST_FILES, serverNumber, serverVsTorrentSeparator, torrent.getId())
        );
    }

    protected String getProgressBar(TorrentEntity torrent) {
        int blocks = 20;
        int blackBlocks = (int) (torrent.getPercentDone() * blocks);
        StringBuilder line = new StringBuilder();

        IntStream.range(0, blackBlocks).mapToObj(i -> "█").forEach(line::append);
        if(blackBlocks < blocks) {
            line.append("▒");
        }
        if (blackBlocks + 1 < blocks) {
            IntStream.range(blackBlocks + 1, blocks).mapToObj(i -> "░").forEach(line::append);
        }

        return "||" + line + "||";
    }

    protected String getGigabytesLeft(TorrentEntity torrent) {
        return torrent.getPercentDone() == 1.0
                ? " (заверш)"
                : " % (" + Math.round((torrent.getTotalSize() - (long) (torrent.getTotalSize() * torrent.getPercentDone())) / 1000000.0) / 1000.0 + " Gb залиш)";
    }

    protected String getStatusIcon(TorrentEntity torrent) {
        return switch (torrent.getStatus()) {
            case 0 -> "⏸";
            case 1 -> "\uD83D\uDD51♾";
            case 2 -> "♾";
            case 3 -> "\uD83D\uDD51⬇️";
            case 4 -> "⬇️";
            case 5 -> "\uD83D\uDD51⬆️";
            case 6 -> "♻️";
            default -> "\uD83C\uDD98";
        };
    }

    @Override
    protected String getQueue() {
        return Queues.Torrent.TORRENTS_LIST;
    }

    @Override
    protected String getRequest() {
        return "get_torrents_list.json";
    }

    @Override
    protected String textOfMessageBegin() {
        return "Торенти ";
    }

    @Override
    protected Predicate<? super TorrentEntity> getSmartFilter(List<String> arguments) {
        return element -> arguments.isEmpty()
                || (arguments.size() == 1 && serverNames.containsKey(arguments.get(0)))
                || arguments
                .stream()
                .anyMatch(a -> ignoreSymbols(element.getName().toUpperCase())
                        .contains(ignoreSymbols(a).toUpperCase())
                );
    }

    private String ignoreSymbols(String str) {
        return str.replaceAll(SYMBOLS_TO_IGNORE, "");
    }
}
