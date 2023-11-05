package com.halushko.kinocat.bot;

import com.halushko.kinocat.bot.handlers.input.SendTextMessageToUser;
import com.halushko.kinocat.bot.handlers.telegram.MyPingHandler;
import com.halushko.kinocat.bot.handlers.telegram.TextHandler;
import com.halushko.kinocat.bot.handlers.telegram.TorrentFileHandler;
import com.halushko.kinocat.core.handlers.input.InputMessageHandler;
import com.halushko.kinocat.core.handlers.telegram.UserMessageHandler;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.*;

@SuppressWarnings("deprecation")
@Slf4j
public class KoTorrentBot extends TelegramLongPollingBot {
    public static KoTorrentBot BOT;

    static {
        for (Map.Entry<String, String> a : System.getenv().entrySet()) {
            log.debug(a.getKey() + " = [" + a.getValue() + "]");
        }
    }

    public static final String BOT_NAME = System.getenv("BOT_NAME");

    public static final String BOT_TOKEN = System.getenv("BOT_TOKEN");

    public static final String BOT_TRUSTED_USERS = System.getenv("BOT_TRUSTED_USERS");

    private static final Set<Long> trustedUserIds = new HashSet<>();

    private static final Collection<UserMessageHandler> handlers = new ArrayList<>() {{
        add(new MyPingHandler());
        add(new TextHandler());
        add(new TorrentFileHandler());
    }};

    public static void main(String[] args) {
        log.debug("Bot starting");
        try {
            TelegramBotsApi botapi = new TelegramBotsApi(DefaultBotSession.class);
            BOT = new KoTorrentBot();
            new Thread(new SendTextMessageToUser()).start();
            botapi.registerBot(BOT);
            log.debug("Bot has been started");
        } catch (Exception e) {
            log.error("Bot start has been fail: ", e);
        }
    }


    @Override
    public void onUpdateReceived(Update update) {
        if (validateUser(update.getMessage().getChatId())) {
            handlers.forEach(i -> i.readMessage(update));
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

    public static boolean validateUser(long userId) {
        if (trustedUserIds.isEmpty()) {
            parseTrustedUsersEnv();
        }
        boolean valid = trustedUserIds.contains(userId);
        log.debug("[validateUser] The user {} is {}valid", userId, valid ? "" : "in");
        return valid;
    }

    public static void sendText(long chatId, String str) {
        if (str == null || str.isEmpty()) {
            return;
        }
        try {
            log.debug("[BOT.sendText] Send text chatId:{}, text:{}", chatId, str);
            BOT.execute(new SendMessage() {{
                            setChatId(chatId);
                            setText(str.replace(InputMessageHandler.OUTPUT_SEPARATOR, "\n"));
                        }}
            );
        } catch (TelegramApiException ex) {
            log.error("Can't send text message to user: ", ex);
        }
    }

    public static void sendText(long chatId, Collection<String> str) {
        for (String line : str) sendText(chatId, line);
    }

    private static void parseTrustedUsersEnv() {
        Arrays.stream(BOT_TRUSTED_USERS.split(",")).forEach(userId -> {
            try {
                trustedUserIds.add(Long.parseLong(userId));
                log.warn(String.format("[BOT.parseTrustedUsersEnv] User ID '%s' is trusted", userId));
            } catch (Exception e) {
                log.warn(String.format("[BOT.parseTrustedUsersEnv] User ID '%s' is invalid", userId));
            }
        });
    }
}
