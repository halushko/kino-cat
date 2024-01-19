package com.halushko.kinocat.core.prcessors;

import com.halushko.kinocat.core.rabbit.SmartJson;
import lombok.val;

import java.util.*;

public abstract class ServicesInfoProcessor {
    public final Map<String, Map<String, String>> values = new LinkedHashMap<>();

    @SuppressWarnings("unchecked")
    public ServicesInfoProcessor(String json) {
        new SmartJson(json)
                .convertToList()
                .stream()
                .map((x -> (Map<String, Object>) x))
                .forEach(x -> values.put(
                                getNameProcessor().process(x),
                                new LinkedHashMap<>() {{
                                    for (val processor : getServiceProcessors()) {
                                        put(processor.key, processor.process(x));
                                    }
                                }}
                        )
                );
    }

    public Map<String, String> getServiceUrls(){
        Map<String, String> result = new LinkedHashMap<>();
        values.forEach((key, value) ->
                result.put(
                        key,
                        String.format(getUrlTemplate(), value.values().toArray())
                )
        );
        return result;
    }
    public abstract ValueProcessor getNameProcessor();
    public abstract List<ValueProcessor> getServiceProcessors();
    public abstract String getUrlTemplate();
}
