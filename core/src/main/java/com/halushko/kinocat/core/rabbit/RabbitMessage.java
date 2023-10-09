package com.halushko.kinocat.core.rabbit;

import lombok.extern.slf4j.Slf4j;

import static com.halushko.kinocat.core.rabbit.RabbitJson.normalizedValue;
import static com.halushko.kinocat.core.rabbit.RabbitMessage.KEYS.*;

@Slf4j
public class RabbitMessage {
    private final RabbitJson json;

    public enum KEYS {
        USER_ID, TEXT, CONSUMER, FILE_NAME, FILE_PATH;
    }

    public RabbitMessage(long userId) {
        json = RabbitJson.create(USER_ID, String.valueOf(userId));
    }

    public RabbitMessage(String message) {
        log.debug(String.format("[RabbitMessage] Start create RabbitMessage text=%s", message));
        json = RabbitJson.create(message);
        log.debug(String.format("[RabbitMessage] Result RabbitMessage for text=%s is json=%s", message, json));
    }

    public RabbitMessage(long userId, String text) {
        log.debug(String.format("[RabbitMessage] Start create RabbitMessage user=%s, text=%s", userId, text));
        text = normalizedValue(text);
        log.debug(String.format("[RabbitMessage] Normalized text=%s", text));
        json = RabbitJson.create(USER_ID, String.valueOf(userId)).add(TEXT, text);
        log.debug(String.format("[RabbitMessage] Result RabbitMessage for user=%s is json=%s", userId, json));
    }

    public String getText() {
        return json.getString(TEXT);
    }

    public long getUserId() {
        return json.getLong(USER_ID);
    }

    public RabbitMessage addValue(String key, String value) {
        log.debug(String.format("[addValue] Start add to json (key, value)=(%s, %s) before_json=%s", key, value, json));
        json.add(key, value);
        log.debug(String.format("[addValue] Result json after adding of (key, value)=(%s, %s) after_json=%s", key, value, json));
        return this;
    }

    public RabbitMessage addValue(KEYS key, String value) {
        return addValue(key.name(), value);
    }

    public String getValue(KEYS key) {
        return getValue(key.name());
    }
    public String getValue(String key) {
        return json.getString(key);
    }

    public String getRabbitMessageText() {
        return json.toString();
    }
    public String getNormalisedText() {
        return RabbitJson.unNormalizeText(json.toString());
    }

    public byte[] getRabbitMessageBytes() {
        return getRabbitMessageText().getBytes();
    }
}
