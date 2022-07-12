package com.halushko.torrent.parsers;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WatchDirectory {
    private volatile static WatchDirectory instance;
    private volatile ScheduledExecutorService executor;

    public WatchDirectory() {
    }

    private static WatchDirectory getInstance() {
        WatchDirectory localInstance = instance;
        if (localInstance == null) {
            synchronized (WatchDirectory.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = new WatchDirectory();
                }
            }
        }
        return instance;
    }

    public void run() {
        ScheduledExecutorService localExecutor = getInstance().executor;
        if (localExecutor == null) {
            synchronized (WatchDirectory.class) {
                localExecutor = instance.executor;
                if (localExecutor == null) {
                    executor = Executors.newSingleThreadScheduledExecutor();
                    executor.scheduleAtFixedRate(WatchDirectory::doPeriodicWork, 0, 10, TimeUnit.SECONDS);
                }
            }
        }
    }

    private static void doPeriodicWork() {
        File dir = new File("/media/torrent/torrent_files");
        File[] files = dir.listFiles((dir1, name) -> name.endsWith(".torrent"));
        if (files != null) {
            for (File torrent : files) {
                new AddTorrent(torrent).run();
                new MoveFile(torrent, torrent.getAbsolutePath() + ".added").run();
            }
        }
    }
}
