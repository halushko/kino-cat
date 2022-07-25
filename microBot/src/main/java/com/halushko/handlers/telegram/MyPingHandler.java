package com.halushko.handlers.telegram;

import com.halushko.KoTorrentBot;
import com.halushko.rabKot.handlers.telegram.PingHandler;

public class MyPingHandler extends PingHandler {
    @Override
    public void sendAnswer(long userId, String messageText) {
        KoTorrentBot.sendText(userId, messageText);
    }
}
