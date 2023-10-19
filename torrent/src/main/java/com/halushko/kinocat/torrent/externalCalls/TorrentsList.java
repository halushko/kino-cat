package com.halushko.kinocat.torrent.externalCalls;

import com.halushko.kinocat.core.cli.Constants;
import com.halushko.kinocat.torrent.entities.TorrentEntity;
import lombok.extern.slf4j.Slf4j;

import java.util.stream.IntStream;

@Slf4j
public class TorrentsList extends GetTorrent {
    public TorrentsList() {
        super();
    }

    @Override
    protected String generateAnswer(TorrentEntity torrent) {
        return String.format("%s %s\n%s %s /more_%s", getStatusIcon(torrent), torrent.getName(), getProgressBar(torrent), getGigabytesLeft(torrent), torrent.getId());
    }
    protected String getProgressBar(TorrentEntity torrent) {
        int blocks = 20;
        int blackBlocks = (int) (torrent.getPercentDone() * blocks);
        StringBuilder line = new StringBuilder();

        IntStream.range(0, blackBlocks).mapToObj(i -> "█").forEach(line::append);
        IntStream.range(blackBlocks, blocks).mapToObj(i -> "░").forEach(line::append);

        return "||" + line + "||";
    }

    protected String getGigabytesLeft(TorrentEntity torrent) {
        return torrent.getPercentDone() == 1.0
                ? " (done)"
                : " % (" + Math.round((torrent.getTotalSize() - (long) (torrent.getTotalSize() * torrent.getPercentDone())) / 1000000.0) / 1000.0 + " Gb left)";
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
        return Constants.Queues.Torrent.EXECUTE_TORRENT_COMMAND_LIST;
    }
}
