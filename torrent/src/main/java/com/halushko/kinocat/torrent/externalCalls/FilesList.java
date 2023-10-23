package com.halushko.kinocat.torrent.externalCalls;

import com.halushko.kinocat.core.cli.Constants;
import com.halushko.kinocat.torrent.entities.SubTorrentEntity;
import com.halushko.kinocat.torrent.entities.TorrentEntity;

import java.util.Comparator;
import java.util.stream.IntStream;

public class FilesList extends GetTorrent {

    @Override
    protected String generateAnswer(TorrentEntity torrent) {
        StringBuilder sb = new StringBuilder();
        torrent.getFiles().stream().sorted(Comparator.comparing(SubTorrentEntity::getName)).forEach(file -> sb.append(getFileInfo(file)).append("\n"));
        return sb.toString();
    }

    protected String getFileInfo(SubTorrentEntity file){
        return String.format("%s\n||%s|| %s", file.getName(), getProgressBar(file), getGigabytesLeft(file));
    }

    protected String getGigabytesLeft(SubTorrentEntity torrent) {
        long completed = torrent.getBytesCompleted();
        long full = torrent.getLength();
        double percents = (double) completed / full;

        return percents == 1.0
                ? " (done)"
                : " % (" + Math.round((full - full * completed) / 1000000.0) / 1000.0 + " Gb left)";
    }

    protected String getProgressBar(SubTorrentEntity torrent) {
        int blocks = 10;
        long completed = torrent.getBytesCompleted();
        long full = torrent.getLength();
        double percents = (double) completed / full;

        int blackBlocks = (int) (percents * blocks);
        StringBuilder line = new StringBuilder();

        IntStream.range(0, blackBlocks).mapToObj(i -> "█").forEach(line::append);
        IntStream.range(blackBlocks, blocks).mapToObj(i -> "░").forEach(line::append);

        return line.toString();
    }

    @Override
    protected String getQueue() {
        return Constants.Queues.Torrent.EXECUTE_TORRENT_COMMAND_LIST_FILES;
    }
}
