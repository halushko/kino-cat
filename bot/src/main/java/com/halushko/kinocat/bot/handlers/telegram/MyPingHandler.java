package com.halushko.kinocat.bot.handlers.telegram;

import com.halushko.kinocat.bot.KoTorrentBot;
import com.halushko.kinocat.core.handlers.telegram.PingHandler;

public class MyPingHandler extends PingHandler {
    @Override
    public void sendAnswer(long userId, String messageText) {
        KoTorrentBot.sendText(userId, messageText);
    }
}
