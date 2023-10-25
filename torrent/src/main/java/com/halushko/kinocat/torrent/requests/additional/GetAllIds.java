package com.halushko.kinocat.torrent.requests.additional;

import com.halushko.kinocat.torrent.Constants;
import com.halushko.kinocat.torrent.entities.TorrentEntity;
import com.halushko.kinocat.torrent.requests.common.GetTorrent;

public class GetAllIds extends GetTorrent {
    @Override
    protected String generateAnswer(TorrentEntity torrent) {
        return torrent.getId();
    }

    @Override
    protected String getQueue() {
        return Constants.Queues.GET_ALL_IDS;
    }

    @Override
    protected String getRequest() {
        return null;
    }
}
