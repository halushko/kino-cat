package com.halushko.handlers.telegram;

import com.halushko.KoTorrentBot;
import com.halushko.rabKot.handlers.telegram.UserMessageHandler;

import java.util.ArrayList;
import java.util.Collection;

public abstract class KoTorrentUserMessageHandler extends UserMessageHandler {
    public static Collection<UserMessageHandler> getHandlers() {
        return new ArrayList<UserMessageHandler>() {{
                add(new TextHandler());
                add(new MyPingHandler());
                add(new TorrentFileHandler());
        }};
    }

    @Override
    public void sendAnswer(long userId, String messageText) {
        KoTorrentBot.sendText(userId, messageText);
    }
}
