package com.halushko.kinocat.bot.handlers.input;

import com.halushko.kinocat.core.cli.Constants;
import com.halushko.kinocat.core.handlers.input.InputMessageHandler;
import com.halushko.kinocat.core.rabbit.RabbitMessage;
import lombok.extern.slf4j.Slf4j;

import static com.halushko.kinocat.bot.KoTorrentBot.sendText;
import static com.halushko.kinocat.core.rabbit.RabbitJson.unNormalizeText;

@Slf4j
public class SendTextMessageToUser extends InputMessageHandler {
    @Override
    protected void getDeliverCallbackPrivate(RabbitMessage message) {
        long chatId = message.getUserId();
        String text = unNormalizeText(message.getText());
        log.debug("[SendTextMessageToUser] Send text chatId:{}, text:{}", chatId, text);
        sendText(chatId, text);
    }
    @Override
    protected String getQueue() {
        return Constants.Queues.Telegram.TELEGRAM_OUTPUT_TEXT;
    }
}
