package com.halushko.kinocat.torrent.requests.concrete;

import com.halushko.kinocat.core.Queues;
import com.halushko.kinocat.torrent.entities.TorrentEntity;
import com.halushko.kinocat.torrent.requests.common.GetTorrent;

public class TorrentFullInfo extends GetTorrent {

    @Override
    protected String generateAnswer(TorrentEntity torrent, String serverNumber, String serverVsTorrentSeparator) {
        StringBuilder sb = new StringBuilder("Торент ").append(torrent.getName()).append("\n/\n");
        sb.append("Маэмо: ").append(Math.round(torrent.getTotalSize() / 1000000.0) / 1000.0).append(" Gb");
        sb.append(" (").append(torrent.getPercentDone() * 100).append("%)\n");
        sb.append("Відвантажено: ").append(Math.round(torrent.getUploadedEver() / 1000000.0) / 1000.0).append(" Gb");
        sb.append(" (").append((double) (10 * torrent.getUploadedEver() / torrent.getTotalSize()) / 10.0).append(")\n");
        sb.append("Остання активнысть: ").append(torrent.getLastActivityDate()).append("\n");
        if(!torrent.getErrorString().isEmpty()) {
            sb.append("Помилка: ").append(torrent.getErrorString()).append("\n");
        }
        sb.append("Торент створено: ").append(torrent.getDateCreated()).append("\n");
        sb.append("Початок закачки: ").append(torrent.getAddedDate()).append("\n");
        if(!torrent.getComment().isEmpty()) {
            sb.append("Інфа: ").append(torrent.getComment());
        }
        return sb.toString();
    }

    @Override
    protected String getQueue() {
        return Queues.Torrent.TORRENT_INFO;
    }

    @Override
    protected String getRequest() {
        return "get_full_info.json";
    }
}
