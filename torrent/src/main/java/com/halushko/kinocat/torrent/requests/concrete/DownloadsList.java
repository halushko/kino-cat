package com.halushko.kinocat.torrent.requests.concrete;

import com.halushko.kinocat.core.Queues;
import com.halushko.kinocat.torrent.entities.TorrentEntity;

import java.util.List;
import java.util.function.Predicate;

public class DownloadsList extends TorrentsList {

    @Override
    protected Predicate<? super TorrentEntity> getSmartFilter(List<String> arguments) {
        return element -> element.getPercentDone() < 1.0
                && (arguments.isEmpty() || (arguments.size() == 1 && serverNames.containsKey(arguments.get(0))));
    }

    @Override
    protected String getQueue() {
        return Queues.Torrent.LIST_TORRENTS_IN_DOWNLOAD_STATUS;
    }
}
