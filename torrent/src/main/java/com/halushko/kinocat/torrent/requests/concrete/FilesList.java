package com.halushko.kinocat.torrent.requests.concrete;

import com.halushko.kinocat.core.Queues;
import com.halushko.kinocat.torrent.entities.SubTorrentEntity;
import com.halushko.kinocat.torrent.entities.TorrentEntity;
import com.halushko.kinocat.torrent.requests.common.GetTorrent;

public class FilesList extends GetTorrent {

    @Override
    protected String generateAnswer(TorrentEntity torrent, String serverNumber, String serverVsTorrentSeparator) {
        StringBuilder sb = new StringBuilder("Торент ").append(torrent.getName()).append("\n/\n");
        SubTorrentEntity previousFile = null;
        for (SubTorrentEntity currentFile : torrent.getFiles()) {
            sb.append(getFileInfo(previousFile, currentFile)).append("\n");
            previousFile = currentFile;
        }
        return sb.toString();
    }

    protected String getFileInfo(SubTorrentEntity previousFile, SubTorrentEntity currentFile) {
        return String.format("%s%s\n||%s|| %s",
                getFolderText(previousFile, currentFile),
                currentFile.getName(),
                getProgressBar(currentFile), getGigabytesLeft(currentFile)
        );
    }

    protected String getGigabytesLeft(SubTorrentEntity torrent) {
        long completed = torrent.getBytesCompleted();
        long full = torrent.getLength();

        return full == completed
                ? " (заверш)"
                : " % (" + Math.round((full - completed) / 1000000.0) / 1000.0 + " Gb залиш)";
    }

    protected String getProgressBar(SubTorrentEntity torrent) {
        int blocks = 10;
        long completed = torrent.getBytesCompleted();
        long full = torrent.getLength();
        double percents = (double) completed / full;
        int blackBlocks = (int) (percents * blocks);

        return constructProgressBar(blocks, blackBlocks);
    }

    protected String getFolderText(SubTorrentEntity previousFile, SubTorrentEntity currentFile) {
        if (currentFile.getFolders().isEmpty()) {
            return "";
        }
        if (currentFile.getFolders().size() == 1 && currentFile.getFolders().get(0).equals(currentFile.getName())) {
            return "";
        }
        if (previousFile != null && previousFile.getFolders().equals(currentFile.getFolders())) {
            return "---";
        }
        if (previousFile == null || !previousFile.getFolders().equals(currentFile.getFolders())) {
            return "/ " + String.join("\n//", currentFile.getFolders()) + "\n---";
        }
        return "";
    }

    @Override
    protected String getQueue() {
        return Queues.Torrent.FILES_LIST;
    }

    @Override
    protected String getRequest() {
        return "file_list.json";
    }
}
