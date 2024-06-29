package com.halushko.kinocat.torrent.requests.concrete;

import com.halushko.kinocat.core.Queues;
import com.halushko.kinocat.torrent.entities.TorrentEntity;

import java.util.List;
import java.util.function.Predicate;

public class SearchByName extends TorrentsList {
    private final static String SYMBOLS_TO_IGNORE = "[\\s._\"']";

    @Override
    protected String getQueue() {
        return Queues.Torrent.SEARCH_BY_NAME;
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
