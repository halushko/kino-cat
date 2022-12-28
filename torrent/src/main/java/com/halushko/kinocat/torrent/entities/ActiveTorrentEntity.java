package com.halushko.kinocat.torrent.entities;

import com.halushko.kinocat.torrent.internalScripts.ViewTorrentInfo;
import org.apache.log4j.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class ActiveTorrentEntity {
    private final static String REGEX_LIST = "\\s*(.+?)\\s{2,}(.+?)%\\s{2,}(.+?)\\s{2,}(.*?)\\s{2,}(.+?)\\s{2,}(.+?)\\s{2,}(.+?)\\s{2,}(\\w+?)\\s{2,}(.+?)$";
    private final static String REGEX_TOTAL_SIZE = ".*Total size:\\s+(.+?)\\s+(\\w+)\\s+\\(.*";

    private final static Pattern PATTERN_LIST = Pattern.compile(REGEX_LIST);
    private final static Pattern PATTERN_TOTAL_SIZE = Pattern.compile(REGEX_TOTAL_SIZE);

    public final String id;
    public double done;
    public String have;
    public String eta;
    public String uploadSpeed;
    public String downloadSpeed;
    public String ratio;
    public String status;
    public String name;

    public Double totalSize;


    public String getStatusIcon() {
        switch (status != null ? status.toUpperCase() : "") {
            case "STOPPED":
                return "\uD83D\uDEAB";
            case "IDLE":
                return done == 100.0 ? "✅" : "\uD83D\uDCA4";
            case "DOWNLOADING":
                return "⬇️";
            case "VERIFYING":
                return "♻";
            case "SEEDING":
                return "⬆️";
            default:
                return "Status=\"" + status + "\"";
        }
    }

    public String getPercents() {
        int blocks = 20;
        int blackBlocks = (int) (done * blocks/ 100);
        StringBuilder line = new StringBuilder();

        IntStream.range(0, blackBlocks).mapToObj(i -> "█").forEach(line::append);
        IntStream.range(blackBlocks, blocks).mapToObj(i -> "░").forEach(line::append);

        return "||" + line + "||"
                + (done == 100.0
                ? " (done)"
                : " % (" + Math.round((totalSize - totalSize * done / 100.0) * 1000.0) / 1000.0 + " Gb left)"
        );
    }

    public ActiveTorrentEntity(String line) {
        Matcher m = PATTERN_LIST.matcher(line);
        Logger.getRootLogger().debug(String.format("[ActiveTorrentEntity] line = %s", line));
        if (m.find()) {
            Logger.getRootLogger().debug("[ActiveTorrentEntity] found");
            id = m.group(1).replaceAll("\\*", "");
            done = Double.parseDouble(m.group(2));
            have = m.group(3);
            eta = m.group(4);
            uploadSpeed = m.group(5);
            downloadSpeed = m.group(6);
            ratio = m.group(7);
            status = m.group(8);
            name = m.group(9);

            Matcher matcher;
            for (String info : ViewTorrentInfo.getInfo(id)) {
                matcher = PATTERN_TOTAL_SIZE.matcher(info);
                if (matcher.find()) {
                    totalSize = Double.parseDouble(matcher.group(1));
                    switch (matcher.group(2).toUpperCase()) {
                        case "KB":
                            totalSize = 0.0;
                        case "MB":
                            totalSize /= 1024;
                    }
                }
            }
        } else {
            id = "-1";
        }
    }
}
