package com.halushko.torrent.parsers;

import halushko.cw3.bot.vareshka.actions.entities.ActiveTorrent;

import java.util.ArrayList;
import java.util.List;

public class ViewTorrentsList extends ExecuteBash {
    public ViewTorrentsList() {
        super("/home/vagrant/commands/list_torrent.sh");
    }

    @Override
    public List<String> run() {
        List<String> output = super.run();

        List<ActiveTorrent> toSort = new ArrayList<>();
        for (String s : output) {
            ActiveTorrent a = new ActiveTorrent(s);
            if (!"-1".equals(a.id)) {
                toSort.add(a);
            }
        }
        toSort.sort((o1, o2) -> o1.status == null ? -1 : o1.status.compareToIgnoreCase(o2.status));
        List<String> torrents = new ArrayList<>();
        for (ActiveTorrent a : toSort) {
            String s1 = a.getStatusIcon() + " " + a.name +"\n" + a.getPercents() + " /commands_" + a.id;
            torrents.add(s1);
        }
        return torrents;
    }
}
