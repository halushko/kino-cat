package com.halushko.kinocat.textConsumer;

import com.halushko.kinocat.middleware.cli.Constants;

interface TextGenerators {
    static String askRemoveTorrent(String args) {
        if (args == null || args.trim().length() == 0) return "Illegal method arguments";
        return "Do you really want to remove torrent no" + args + "?\n" +
                "-- remove only torrent: " + Constants.Commands.Text.REMOVE_COMMAND + args + "\n\n" +
                "-- remove with files: " + Constants.Commands.Text.REMOVE_COMMAND + args;
    }
}
