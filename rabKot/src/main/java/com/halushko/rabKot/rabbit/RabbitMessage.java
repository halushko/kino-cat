package com.halushko.rabKot.rabbit;

import static com.halushko.rabKot.rabbit.RabbitMessage.KEYS.*;

public class RabbitMessage {
    private final RabbitJson json;

    public enum KEYS {
        USER_ID, TEXT, CONSUMER, FILE_NAME, FILE_PATH;
    }

    public RabbitMessage(long userId) {
        json = RabbitJson.create(USER_ID, String.valueOf(userId));
    }

    public RabbitMessage(String message) {
        json = RabbitJson.create(message);
    }

    public RabbitMessage(long userId, String text) {
        json = RabbitJson.create(USER_ID, String.valueOf(userId)).add(TEXT, text);
    }

    public String getText() {
        return json.getString(TEXT);
    }

    public long getUserId() {
        return json.getLong(USER_ID);
    }

    public RabbitMessage addValue(String key, String value) {
        json.add(key, value);
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

    public byte[] getRabbitMessageBytes() {
        return getRabbitMessageText().getBytes();
    }
}
