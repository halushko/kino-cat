package com.halushko.kinocat.torrent.requests.concrete;

import com.halushko.kinocat.core.commands.Constants;
import com.halushko.kinocat.torrent.requests.common.VoidTorrentOperator;

public class DeleteWithFiles extends VoidTorrentOperator {
    @Override
    protected String getCommandName() {
        return "видалення торента та всіх файлів, пов'язаних з ним";
    }

    @Override
    protected String getRequest() {
        return "remove_with_files.json";
    }

    @Override
    protected String getQueue() {
        return Constants.Queues.Torrent.DELETE_WITH_FILES;
    }
}
