package com.halushko.kinocat.core.rabbit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.*;

@SuppressWarnings({"UnusedReturnValue", "unused"})
@Slf4j
public class RabbitMessage {
    public enum KEYS {
        USER_ID, TEXT, CONSUMER, FILE_NAME, FILE_PATH
    }

    private String json = "";
    private final static ObjectMapper mapper = new ObjectMapper() {{
        enable(SerializationFeature.INDENT_OUTPUT);
    }};

    public RabbitMessage(long userId) {
        addValue(KEYS.USER_ID, String.valueOf(userId));
    }
    public RabbitMessage(String key, Object value) {
        addValue(key, value);
    }

    public RabbitMessage(long userId, String text) {
        log.debug("[RabbitMessage] Start create RabbitMessage user={}, text={}", userId, text);
        addValue(KEYS.USER_ID, String.valueOf(userId));
        addValue(KEYS.TEXT, text);
        log.debug("[RabbitMessage] Result RabbitMessage for user={} is json={}", userId, json);
    }

    public RabbitMessage(String json) {
        this.json = json;
    }

    public RabbitMessage addValue(KEYS key, String value) {
        return addValue(key.name(), value);
    }
    public RabbitMessage addValue(String key, Object value) {
        log.debug("[addValue] Start add to json (key, value)=({}, {}) before_json={}", key, value, json);
        val map = convertJsonToMap(json);
        map.put(key, value);
        json = convertToString(map);
        return this;
    }

    public RabbitMessage getValue(KEYS key) {
        return getValue(key.name());
    }
    public RabbitMessage getValue(String key) {
        return new RabbitMessage(getStringValue(key));
    }

    public String getStringValue(KEYS key) {
        return getStringValue(key.name());
    }
    public String getStringValue(String key) {
        return convertToString(convertJsonToMap(json).get(key));
    }

    public String getText() {
        return getStringValue(KEYS.TEXT);
    }

    public long getUserId() {
        return Long.parseLong(getStringValue(KEYS.USER_ID));
    }

    public String getRabbitMessageText(){
        return json;
    }

    public byte[] getRabbitMessageBytes() {
        return getRabbitMessageText().getBytes();
    }

    private static String convertToString(final Object value) {
        if(String.valueOf(value).equalsIgnoreCase("null")) return "";
        String result = "!!!Error!!!";
        try {
            result = value instanceof String ? (String)value : mapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            log.error("[convertToString] error={}", e.getMessage());
        }
        log.debug("[convertToString] result={}", result);
        return result;
    }

    private static Map<String, Object> convertJsonToMap(String json) {
        Map<String, Object> result = new LinkedHashMap<>();
        try {
            result = convertJsonToMap(mapper.readTree(json));
        } catch (JsonProcessingException e) {
            log.error("[convertJsonToMap] error={}", e.getMessage());
        }
        log.debug("[convertJsonToMap] result={}", result);
        return result;
    }

    private static Map<String, Object> convertJsonToMap(JsonNode jsonNode) {
        Map<String, Object> resultMap = new HashMap<>();
        jsonNode.fields().forEachRemaining(entry -> resultMap.put(entry.getKey(), parseJsonNode(entry.getValue())));
        return resultMap;
    }

    private static Object parseJsonNode(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            return convertJsonToMap(jsonNode);
        } else if (jsonNode.isArray()) {
            List<Object> list = new ArrayList<>();
            for (JsonNode entry : jsonNode) {
                list.add(parseJsonNode(entry));
            }
            return list;
        } else {
            return jsonNode.asText();
        }
    }
}
