package com.halushko.kinocat.bot;

import com.halushko.kinocat.bot.handlers.input.SendTextMessageToUser;
import com.halushko.kinocat.bot.handlers.telegram.MyPingHandler;
import com.halushko.kinocat.bot.handlers.telegram.TextHandler;
import com.halushko.kinocat.bot.handlers.telegram.TorrentFileHandler;
import com.halushko.kinocat.middleware.handlers.telegram.UserMessageHandler;
import org.apache.log4j.Logger;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class KoTorrentBot extends TelegramLongPollingBot {
    public static KoTorrentBot BOT;

    static {
        for (Map.Entry<String, String> a: System.getenv().entrySet()){
            Logger.getRootLogger().debug(a.getKey() + " = [" + a.getValue() + "]");
        }
    }
    public static final String BOT_NAME = System.getenv("BOT_NAME");

    public static final String BOT_TOKEN = System.getenv("BOT_TOKEN");

    public static void main(String[] args) {
        try {
            ApiContextInitializer.init();
            TelegramBotsApi botapi = new TelegramBotsApi();
            BOT = new KoTorrentBot();
            new Thread(new SendTextMessageToUser()).start();
            botapi.registerBot(BOT);
        } catch (Exception e) {
            Logger.getRootLogger().error("Bot start has been fail: ", e);
        }
    }


    @Override
    public void onUpdateReceived(Update update) {
        Collection<UserMessageHandler> handlers = new ArrayList<UserMessageHandler>() {{
            add(new MyPingHandler());
            add(new TextHandler());
            add(new TorrentFileHandler());
        }};
        for (UserMessageHandler handler : handlers) {
            handler.readMessage(update);
        }
    }

    @Override
    public String getBotUsername() {
        return BOT_NAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    public static void sendText(long chatId, String str) {
        if (str == null || str.isEmpty()) {
            return;
        }
        try {
            BOT.execute(new SendMessage() {{
                            setChatId(chatId);
                            setText(str);
                        }}
            );
        } catch (TelegramApiException ex) {
            Logger.getRootLogger().error("Can't send text message to user: ", ex);
        }
    }

    public static void sendText(long chatId, Collection<String> str) {
        for (String line : str) sendText(chatId, line);
    }
}
