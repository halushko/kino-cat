package com.halushko.kinocat.textConsumer;

import static com.halushko.kinocat.core.cli.Constants.Commands.Torrent.REMOVE_JUST_TORRENT;
import static com.halushko.kinocat.core.cli.Constants.Commands.Torrent.REMOVE_WITH_FILES;

interface TextGenerators {
    static String askRemoveTorrent(String args) {
        if (args == null || args.trim().length() == 0) return "Illegal method arguments";
        return "Do you really want to remove torrent no " + args + "?\n\n" +
                "-- remove only torrent: " + REMOVE_JUST_TORRENT + args + "\n\n" +
                "-- remove with files: " + REMOVE_WITH_FILES + args;
    }
}
