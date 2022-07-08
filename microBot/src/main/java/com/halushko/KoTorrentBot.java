package com.halushko;

import com.halushko.handlers.input.SendTextMessageToUser;
import com.halushko.handlers.telegram.KoTorrentUserMessageHandler;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Collection;

public class KoTorrentBot extends TelegramLongPollingBot {
    public static KoTorrentBot BOT;

    public static void main(String[] args) {
        ApiContextInitializer.init();
        TelegramBotsApi botapi = new TelegramBotsApi();

        try {
            BOT = new KoTorrentBot();
            new Thread(new SendTextMessageToUser()).start();;
            botapi.registerBot(BOT);
        } catch (TelegramApiException e) {
            System.out.println("Bot start has been fail: " + e.getMessage());
        }
    }


    @Override
    public void onUpdateReceived(Update update) {
        KoTorrentUserMessageHandler.getHandlers().forEach(handler -> handler.readMessage(update));
    }

    @Override
    public String getBotUsername() {
        return "koTorrentBot";
    }

    @Override
    public String getBotToken() {
        return "1859184957:AAF2DaTJpaOEj2I6T2Vf35fOJszp9skPeiE";
    }

    public static void sendText(long chatId, String str) {
        if (str == null || str.isEmpty()) {
            return;
        }
        try {
            BOT.execute(
                    new SendMessage() {{
                        setChatId(chatId);
                        setText(str);
                    }}
            );
        } catch (TelegramApiException ex) {
            System.out.println("Unknown koTorrent error: " + ex.getMessage());
        }
    }

    public static void sendText(long chatId, Collection<String> str) {
        for (String line : str) sendText(chatId, line);
    }
}