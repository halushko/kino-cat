package com.halushko.kinocat.torrent.requests.common;

import com.halushko.kinocat.core.rabbit.SmartJson;
import com.halushko.kinocat.torrent.entities.TorrentEntity;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.IntStream;

public abstract class GetTorrent extends TransmissionWebApiExecutor {
    @Override
    protected final List<String> parceResponce(SmartJson json) {
        return json.getSubMessage(SmartJson.KEYS.OUTPUT)
                .getSubMessage("arguments")
                .getSubMessage("torrents")
                .convertToList()
                .stream()
                .map(TorrentEntity::new)
                .filter(getSmartFilter(
                                json.getSubMessage(SmartJson.KEYS.INPUT)
                                        .getValue(SmartJson.KEYS.COMMAND_ARGUMENTS)
                                        .toUpperCase()
                        )
                )
                .sorted(Comparator.comparing(TorrentEntity::getName))
                .map(this::generateAnswer)
                .toList();
    }

    protected abstract String generateAnswer(TorrentEntity torrent);

    @Override
    protected final Object[] getRequestArguments(SmartJson message) {
        return message == null ? new Object[0] : filterByIndex(message);
    }

    protected Object[] filterByIndex(SmartJson message) {
        Object[] arguments = super.getRequestArguments(message);
        HashSet<Integer> excludedIndexes = getNotRequestArgumentIndexes();
        return IntStream.range(0, arguments.length)
                .filter(i -> !excludedIndexes.contains(i))
                .mapToObj(x -> arguments[x])
                .toArray(Object[]::new);
    }

    protected HashSet<Integer> getNotRequestArgumentIndexes() {
        return new HashSet<>();
    }

    @SuppressWarnings("unused")
    protected Predicate<? super TorrentEntity> getSmartFilter(String arguments) {
        return element -> true;
    }
}
