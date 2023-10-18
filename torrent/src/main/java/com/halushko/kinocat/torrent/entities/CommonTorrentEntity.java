package com.halushko.kinocat.torrent.entities;

import com.halushko.kinocat.core.cli.Constants;
import com.halushko.kinocat.core.rabbit.SmartJson;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.Map;
import java.util.stream.IntStream;

@Slf4j
public class CommonTorrentEntity {
    public final String id;
    private final double percentDone;
    private final int status;
    public final String name;
    private final long totalSize;

    public CommonTorrentEntity(Map<String, Object> map){
        val torrent = new SmartJson(map);
        String status = torrent.getValue("status");
        this.status = status.isEmpty() ? -1 : Integer.parseInt(status);
        this.name = torrent.getValue("name");
        String percentDone = torrent.getValue("percentDone");
        this.percentDone = percentDone.isEmpty() ? 0.0 : Double.parseDouble(percentDone);
        String totalSize = torrent.getValue("totalSize");
        this.totalSize = percentDone.isEmpty() ? 0L : Long.parseLong(totalSize);
        this.id = torrent.getValue("id");
    }

    public String generateTorrentCommonInfo(){
        return String.format("%s %s\n%s %s /more_%s", status, name, getProgressBar(), getGigabytesLeft(), id);
    }

    public String generateTorrentCommands(){
        return String.format("%s\n%s%s\n%s%s\n%s%s\n%s%s", name,
                Constants.Commands.Torrent.PAUSE, id,
                Constants.Commands.Torrent.RESUME, id,
                Constants.Commands.Torrent.TORRENT_INFO, id,
                Constants.Commands.Text.REMOVE_COMMAND, id);
    }

    protected String getProgressBar() {
        int blocks = 20;
        int blackBlocks = (int) (percentDone * blocks);
        StringBuilder line = new StringBuilder();

        IntStream.range(0, blackBlocks).mapToObj(i -> "█").forEach(line::append);
        IntStream.range(blackBlocks, blocks).mapToObj(i -> "░").forEach(line::append);

        return "||" + line + "||";
    }

    protected String getGigabytesLeft() {
        return percentDone == 1.0
                ? " (done)"
                : " % (" + Math.round((totalSize - (long) (totalSize * percentDone)) / 1000000.0) / 1000.0 + " Gb left)";
    }

    protected String getStatusIcon() {
        return switch (status) {
            case 0 -> "⏸";
            case 1 -> "\uD83D\uDD51♾";
            case 2 -> "♾";
            case 3 -> "\uD83D\uDD51⬇️";
            case 4 -> "⬇️";
            case 5 -> "\uD83D\uDD51⬆️";
            case 6 -> "♻\uFE0F";
            default -> "\uD83C\uDD98";
        };
    }
}
