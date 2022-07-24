package com.halushko.handlers.telegram;

import com.halushko.rabKot.handlers.telegram.UserMessageHandler;

import java.util.ArrayList;
import java.util.Collection;

public abstract class KoTorrentUserMessageHandler {
    public static Collection<UserMessageHandler> getHandlers() {
        return new ArrayList<UserMessageHandler>() {{
            add(new MyPingHandler());
            add(new TextHandler());
            add(new TorrentFileHandler());
        }};
    }
}
