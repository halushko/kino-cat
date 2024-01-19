package com.halushko.kinocat.core.prcessors;

import java.util.Map;

public class ValueProcessor {
    public final String key, defaultValue;

    public ValueProcessor(String key, String defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
    }

    public final String process(Map<String, Object> toAnalise) {
        return String.valueOf(toAnalise.getOrDefault(key, defaultValue));
    }
}
