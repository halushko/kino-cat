package com.halushko.kinocat.torrent.requests.concrete;

import com.halushko.kinocat.torrent.entities.TorrentEntity;

import java.util.List;
import java.util.function.Predicate;

public class DownloadsList extends TorrentsList {

    @Override
    protected Predicate<? super TorrentEntity> getSmartFilter(List<String> arguments) {
        return element -> arguments.isEmpty()
                || (arguments.size() == 1 && serverNames.containsKey(arguments.get(0)))
                || arguments
                .stream()
                .anyMatch(a -> element.getPercentDone() < 1.0);
    }
}
