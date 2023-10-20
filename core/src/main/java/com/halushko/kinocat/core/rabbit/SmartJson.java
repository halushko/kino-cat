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
public class SmartJson {
    public static final String DEFAULT_KEY = "DEFAULT_KEY";

    public enum KEYS {
        USER_ID, TEXT, CONSUMER, FILE_NAME, FILE_PATH
    }

    private String json = "";
    private final static ObjectMapper mapper = new ObjectMapper() {{
        enable(SerializationFeature.INDENT_OUTPUT);
    }};

    public SmartJson(long userId) {
        addValue(KEYS.USER_ID, String.valueOf(userId));
    }

    public SmartJson(String key, Object value) {
        addValue(key, value);
    }

    public SmartJson(long userId, String text) {
        log.debug("[RabbitMessage] Start create RabbitMessage user={}, text={}", userId, text);
        addValue(KEYS.USER_ID, String.valueOf(userId));
        addValue(KEYS.TEXT, text);
        log.debug("[RabbitMessage] Result RabbitMessage for user={} is json={}", userId, json);
    }

    public SmartJson(String json) {
        this.json = json == null ? "" : json;
    }

    public SmartJson(Map<String, Object> map) {
        this.json = map == null ? "" : convertToString(map);
    }

    public SmartJson addValue(KEYS key, String value) {
        return addValue(key.name(), value);
    }

    public SmartJson addValue(String key, Object value) {
        log.debug("[addValue] Start add to json (key, value)=({}, {}) before_json={}", key, value, json);
        val map = convertJsonToMap(json);
        map.put(key, value);
        json = convertToString(map);
        return this;
    }

    public SmartJson getSubMessage(KEYS key) {
        return getSubMessage(key.name());
    }

    public SmartJson getSubMessage(String key) {
        return new SmartJson(getValue(key));
    }

    public String getValue(KEYS key) {
        return getValue(key.name());
    }

    public String getValue(String key) {
        return convertToString(convertJsonToMap(json).getOrDefault(key, ""));
    }

    public Map<String, Object> convertToMap() {
        return convertJsonToMap(json);
    }

    public List<Object> convertToList() {
        val map = convertJsonToMap(json);
        if (map.isEmpty()){
            return Collections.emptyList();
        } else if (map.size() == 1) {
            Object value = map.values().stream().findFirst().get();
            if (value instanceof List) {
                //noinspection unchecked
                return (List<Object>) value;
            } else {
                log.error("[convertToList] The requested string is not an Array. Json={}", json);
            }
        } else {
            log.error("[convertToList] There are more than one nodes, but expected the only one and Array. Json={}", json);
        }
        throw new RuntimeException();
    }

    public String getText() {
        return getValue(KEYS.TEXT);
    }

    public long getUserId() {
        return Long.parseLong(getValue(KEYS.USER_ID));
    }

    public String getRabbitMessageText() {
        return json;
    }

    public byte[] getRabbitMessageBytes() {
        return getRabbitMessageText().getBytes();
    }

    private static String convertToString(final Object value) {
        if (String.valueOf(value).equalsIgnoreCase("null")) return "";
        String result = "!!!Error!!!";
        try {
            result = value instanceof String ? (String) value : mapper.writeValueAsString(value);
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
        if (jsonNode.isObject()) {
            jsonNode.fields().forEachRemaining(entry -> resultMap.put(entry.getKey(), parseJsonNode(entry.getValue())));
        } else if (jsonNode.isArray()) {
            List<Object> list = new ArrayList<>();
            for (JsonNode entry : jsonNode) {
                list.add(parseJsonNode(entry));
            }
            resultMap.put(DEFAULT_KEY, list);
        } else {
            resultMap.put(DEFAULT_KEY, jsonNode.asText());
        }

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
