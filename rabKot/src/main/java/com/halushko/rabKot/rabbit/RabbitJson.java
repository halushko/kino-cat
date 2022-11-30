package com.halushko.rabKot.rabbit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class RabbitJson {
    private final static String EMPTY_KEY = "EMPTY_KEY";
    public final static double DEFAULT_DOUBLE_VALUE = 0;
    public final static int DEFAULT_INT_VALUE = 0;
    public final static String DEFAULT_STRING_VALUE = "";

    private IRabbitJson rj;
    private RabbitJson parent;

    private RabbitJson(IRabbitJson rabbitJson) {
        rj = rabbitJson;
        rj.owner = this;
    }

    public static RabbitJson create(String key, String value) {
        key = normalizedKey(key);
        IRabbitJson json = normalizeValue(new ValueRabbitJson(value));
        if (key.equals(EMPTY_KEY)) {
            return new RabbitJson(json);
        } else {
            return new RabbitJson(new MapRabbitJson().add(key, json));
        }
    }

    public static RabbitJson create(RabbitMessage.KEYS key, String value) {
        return create(key.name(), value);
    }

    public static RabbitJson create(String string) {
        string = normalizedValue(string);
        JSONObject json = toJsonObject(string);
        if (json != null) {
            MapRabbitJson rabbit = new MapRabbitJson();
            for (Iterator<String> it = json.keys(); it.hasNext(); ) {
                String key = normalizedKey(it.next());
                String value = json.optString(key);
                value = unNormalizedValue(value);
                IRabbitJson jsonValue = create(value).rj;
                rabbit.map.put(key, jsonValue);
            }
            return new RabbitJson(rabbit);
        }
        JSONArray array = toJsonArray(string);
        if (array != null) {
            ArrayRabbitJson newArray = new ArrayRabbitJson(array);
            if (newArray.values.size() > 1) {
                return new RabbitJson(newArray);
            } else if (newArray.values.size() == 1) {
                String value = newArray.values.iterator().next().toString();
                value = unNormalizedValue(value);
                return new RabbitJson(new ValueRabbitJson(value));
            } else {
                return new RabbitJson(new ValueRabbitJson(""));
            }
        }
        return new RabbitJson(new ValueRabbitJson(unNormalizedValue(string)));
    }

    public static RabbitJson create(Collection<String> values) {
        List<String> nvalues = values.stream().map(RabbitJson::normalizedValue).collect(Collectors.toList());
        return create(new JSONArray(nvalues).toString());
    }

    public static RabbitJson create(String key, Collection<String> values) {
        List<String> nvalues = values.stream().map(RabbitJson::normalizedValue).collect(Collectors.toList());
        return create(key, new JSONArray(nvalues).toString());
    }

    public static RabbitJson create(RabbitMessage.KEYS key, Collection<String> values) {
        return create(key.name(), values);
    }

    public RabbitJson getParent() {
        return parent == null ? this : parent;
    }

    public RabbitJson add(String value) {
        return add("", normalizedValue(value));
    }

    public RabbitJson add(RabbitMessage.KEYS key, String value) {
        return add(key.name(), value);
    }

    public RabbitJson add(String key, String value) {
        value = value == null ? "" : value;
        value = value.trim();
        key = normalizedKey(key);
        IRabbitJson me = this.rj;
        IRabbitJson that = create(value).rj;
        if (me instanceof MapRabbitJson) {
            me.add(key, that);
        } else {
            MapRabbitJson otherMe = new MapRabbitJson();
            otherMe.map.put(EMPTY_KEY, me);
            me = otherMe.add(key, that);
        }
        rj = normalizeValue(me);
        return this;
    }

    public RabbitJson removeKey(String key) {
        rj = normalizeValue(rj.removeKey(EMPTY_KEY));
        return this;
    }

    public RabbitJson removeValue(String value) {
        return remove(EMPTY_KEY, normalizedValue(value));
    }

    public RabbitJson remove(RabbitMessage.KEYS key, String value) {
        return remove(key.name(), value);
    }

    public RabbitJson remove(String key, String value) {
        rj = normalizeValue(rj.remove(key, new ValueRabbitJson(value)));
        return this;
    }

    public RabbitJson get() {
        return get("");
    }

    public RabbitJson get(RabbitMessage.KEYS key) {
        return get(key.name());
    }

    public RabbitJson get(String key) {
        key = normalizedKey(key);
        if (rj instanceof MapRabbitJson) {
            IRabbitJson value = ((MapRabbitJson) rj).map.get(key);
            RabbitJson newOwner = new RabbitJson(value);
            newOwner.parent = this;
            return value.owner != null ? value.owner : newOwner;
        } else {
            return this;
        }
    }

    public long getLong() {
        try {
            return Long.parseLong(getString());
        } catch (Exception e) {
            return DEFAULT_INT_VALUE;
        }
    }

    public int getInteger() {
        try {
            return Integer.getInteger(getString());
        } catch (Exception e) {
            return DEFAULT_INT_VALUE;
        }
    }

    public double getDouble() {
        try {
            return Double.parseDouble(getString());
        } catch (Exception ignored) {
            return DEFAULT_DOUBLE_VALUE;
        }
    }

    public String getString() {
        IRabbitJson me = normalizeValue(rj);
        if (me instanceof ValueRabbitJson) {
            return me.toString();
        } else {
            return DEFAULT_STRING_VALUE;
        }
    }

    public long getLong(RabbitMessage.KEYS key) {
        return getLong(key.name());
    }

    public long getLong(String key) {
        try {
            return Long.parseLong(getString(key));
        } catch (Exception e) {
            return DEFAULT_INT_VALUE;
        }
    }

    public int getInteger(RabbitMessage.KEYS key) {
        return getInteger(key.name());
    }

    public int getInteger(String key) {
        try {
            return Integer.getInteger(getString(key));
        } catch (Exception e) {
            return DEFAULT_INT_VALUE;
        }
    }

    public double getDouble(RabbitMessage.KEYS key) {
        return getDouble(key.name());
    }

    public double getDouble(String key) {
        try {
            return Double.parseDouble(getString(key));
        } catch (Exception ignored) {
            return DEFAULT_DOUBLE_VALUE;
        }
    }

    public String getString(String key) {
        key = normalizedKey(key);
        if (rj instanceof MapRabbitJson) {
            Map<String, IRabbitJson> map = ((MapRabbitJson) normalizeValue(rj)).map;
            if (map.containsKey(key)) {
                return map.get(key).toString();
            }
        }
        return DEFAULT_STRING_VALUE;
    }

    public String getString(RabbitMessage.KEYS key) {
        return getString(key.name());
    }

    private static String normalizedKey(String key) {
        if (key != null) key = key.trim();
        return key == null || key.equals("") || key.equalsIgnoreCase("null") ? EMPTY_KEY : key;
    }

    public static String normalizedValue(String value) {
        value = value.replace("\n", "@nnn@")
                .replace("\r", "@rrr@")
                .replace("\t", "@ttt@");
        value = value.replaceAll("\"", "@kkk@").
                replace("[", "@lkv@").
                replace("]", "@rkv@").
                replace("{", "@lfig@").
                replace("}", "@rfig@");
        value = value.replace("\\", "@backsl@");
        value = value.replaceAll("@lfig@@kkk@", "{\"").replaceAll("@kkk@@rfig@", "\"}").
                replaceAll("@lkv@\\s*@kkk@", "[\"").replaceAll("@kkk@\\s*@rkv@", "\"]").
                replaceAll("@kkk@\\s*,\\s*@kkk@", "\",\"").replaceAll("@kkk@\\s*:\\s*@kkk@", "\":\"");
        return value;
    }

    private static String unNormalizedValue(String value) {
//        value = value.replace("@nnn@", "\n").replace("@rrr@", "\r").replace("@ttt@", "\t");
//        value = value.replace("@kkk@", "\"");
        return value;
    }

    public static String unNormalizeText(String value) {
        value = value.replace("@nnn@", "\n").replace("@rrr@", "\r").replace("@ttt@", "\t");
        value = value.replace("@kkk@", "\"");
        value = value.replace("@backsl@", "\\");
        value = value.replace("@lkv@", "[").replace("@rkv@", "]");
        value = value.replace("@lfig@", "{").replace("@rfig@", "}");
        return value;
    }

    private static IRabbitJson normalizeValue(IRabbitJson value) {
        if (value instanceof MapRabbitJson) {
            MapRabbitJson mapRabbitJson = (MapRabbitJson) value;
            if (mapRabbitJson.map.size() == 1 && mapRabbitJson.map.containsKey(EMPTY_KEY)) {
                return normalizeValue(mapRabbitJson.map.get(EMPTY_KEY), mapRabbitJson.owner);
            }
        }
        return value;
    }

    private static IRabbitJson normalizeValue(IRabbitJson value, RabbitJson owner) {
        if (value instanceof MapRabbitJson) {
            MapRabbitJson mapRabbitJson = (MapRabbitJson) value;
            if (mapRabbitJson.map.size() == 1 && mapRabbitJson.map.containsKey(EMPTY_KEY)) {
                IRabbitJson a = normalizeValue(mapRabbitJson.map.get(EMPTY_KEY), owner);
                a.owner = owner;
                return a;
            }
        }
        return value;
    }

    private static JSONObject toJsonObject(String test) {
        try {
            return new JSONObject(test);
        } catch (JSONException ignore) {
            return null;
        }
    }

    private static JSONArray toJsonArray(String test) {
        try {
            return new JSONArray(test);
        } catch (JSONException ignore) {
            return null;
        }
    }

    @Override
    public String toString() {
        return rj.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return normalizeValue(rj).equals(normalizeValue(((RabbitJson) o).rj));
    }

    @Override
    public int hashCode() {
        return Objects.hash(rj);
    }

    private static abstract class IRabbitJson {
        public RabbitJson owner;

        public abstract IRabbitJson add(IRabbitJson value);

        public abstract IRabbitJson add(String key, IRabbitJson value);

        public abstract IRabbitJson clear();

        public abstract IRabbitJson removeKey(String key);

        public abstract IRabbitJson removeValue(IRabbitJson value);

        public abstract IRabbitJson remove(String key, IRabbitJson value);

    }

    static class ValueRabbitJson extends IRabbitJson {
        private String value;

        private ValueRabbitJson(String string) {
            string = string == null ? DEFAULT_STRING_VALUE : string.trim();
            string = string.equalsIgnoreCase("null") ? DEFAULT_STRING_VALUE : string;
            value = string;
        }

        @Override
        public IRabbitJson add(IRabbitJson value) {
            return add(EMPTY_KEY, value);
        }

        @Override
        public IRabbitJson add(String key, IRabbitJson value) {
            key = normalizedKey(key);
            return normalizeValue(new MapRabbitJson().add(EMPTY_KEY, this).add(key, value));
        }

        @Override
        public IRabbitJson clear() {
            value = "";
            return this;
        }

        @Override
        public IRabbitJson removeKey(String key) {
            key = normalizedKey(key);
            return EMPTY_KEY.equals(key) ? clear() : this;
        }

        @Override
        public IRabbitJson removeValue(IRabbitJson value) {
            return remove(EMPTY_KEY, normalizeValue(value));
        }

        @Override
        public IRabbitJson remove(String key, IRabbitJson value) {
            key = normalizedKey(key);
            IRabbitJson valueToRemove = normalizeValue(value);
            if (EMPTY_KEY.equals(key)) {
                if (valueToRemove instanceof ValueRabbitJson && this.equals(valueToRemove)) {
                    clear();
                } else if (valueToRemove instanceof ArrayRabbitJson) {
                    for (IRabbitJson v : ((ArrayRabbitJson) valueToRemove).values) {
                        if (v instanceof ValueRabbitJson) {
                            removeValue(v);
                        }
                    }
                }
            }
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            return value.equals(((ValueRabbitJson) o).value);
        }

        @Override
        public String toString() {
            return value;
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }
    }

    private static class MapRabbitJson extends IRabbitJson {
        private final Map<String, IRabbitJson> map = new LinkedHashMap<>();

        public IRabbitJson add(IRabbitJson value) {
            return add("", value);
        }

        public IRabbitJson add(String key, IRabbitJson value) {
            key = normalizedKey(key);
            IRabbitJson newValue = normalizeValue(value);
            if (map.containsKey(key)) {
                IRabbitJson currentValue = map.get(key);
                if (currentValue instanceof ArrayRabbitJson) {
                    ((ArrayRabbitJson) currentValue).values.add(newValue);
                } else if (!currentValue.equals(newValue)) {
                    map.remove(key);
                    ArrayRabbitJson array = new ArrayRabbitJson();
                    array.add(currentValue).add(newValue);
                    map.put(key, array);
                }
            } else {
                map.put(key, newValue);
            }
            return this;
        }

        @Override
        public IRabbitJson clear() {
            map.clear();
            return this;
        }

        @Override
        public IRabbitJson removeKey(String key) {
            key = normalizedKey(key);
            map.remove(key);
            return this;
        }

        @Override
        public IRabbitJson removeValue(IRabbitJson value1) {
            Collection<String> keysToRemove = new HashSet<>();
            IRabbitJson valueToRemove = normalizeValue(value1);

            map.forEach((k, v) -> {
                if (v.equals(valueToRemove)) {
                    keysToRemove.add(k);
                }
            });
            keysToRemove.forEach(map::remove);
            return this;
        }

        @Override
        public IRabbitJson remove(String key, IRabbitJson value1) {
            key = normalizedKey(key);
            IRabbitJson valueToRemove = normalizeValue(value1);
            if (map.containsKey(key)) {
                if (valueToRemove instanceof ValueRabbitJson) {
                    map.remove(key, valueToRemove);
                } else if (valueToRemove instanceof ArrayRabbitJson) {
                    IRabbitJson myValue = map.get(key);
                    if (myValue instanceof ValueRabbitJson) {
                        for (IRabbitJson v : ((ArrayRabbitJson) valueToRemove).values) {
                            if (myValue.equals(v)) {
                                map.remove(key);
                            }
                        }
                    } else if (myValue instanceof ArrayRabbitJson) {
                        for (IRabbitJson v : ((ArrayRabbitJson) valueToRemove).values) {
                            ((ArrayRabbitJson) valueToRemove).values.remove(v);
                        }
                    }
                } else {
                    IRabbitJson myValue = map.get(key);
                    if (myValue instanceof MapRabbitJson) {
                        for (Map.Entry<String, IRabbitJson> kv : ((MapRabbitJson) valueToRemove).map.entrySet()) {
                            if (myValue.equals(kv.getValue())) {
                                this.map.remove(key);
                            }
                        }
                    }
                }
            }
            return this;
        }

        @Override
        public String toString() {
            if (map.isEmpty()) return DEFAULT_STRING_VALUE;
            IRabbitJson me = normalizeValue(this);
            if (me instanceof ArrayRabbitJson || me instanceof ValueRabbitJson) {
                return me.toString();
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("{");
                boolean comma = false;
                for (Map.Entry<String, IRabbitJson> keyValue : ((MapRabbitJson) me).map.entrySet()) {
                    if (comma) {
                        sb.append(",");
                    } else {
                        comma = true;
                    }
                    sb.append("\"").append(keyValue.getKey()).append("\":");
                    String quotes = keyValue.getValue() instanceof ValueRabbitJson ? "\"" : "";
                    sb.append(quotes).append(keyValue.getValue()).append(quotes);
                }
                sb.append("}");
                return sb.toString();
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            MapRabbitJson that = (MapRabbitJson) normalizeValue((IRabbitJson) o);
            MapRabbitJson me = (MapRabbitJson) normalizeValue(this);
            if (map.size() != that.map.size()) return false;
            for (String key : me.map.keySet()) {
                if (!that.map.containsKey(key) || !me.map.get(key).equals(that.map.get(key))) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public int hashCode() {
            return Objects.hash(map);
        }
    }

    private static class ArrayRabbitJson extends IRabbitJson {
        private final Collection<IRabbitJson> values = new LinkedHashSet<>();

        private ArrayRabbitJson() {

        }

        private ArrayRabbitJson(JSONArray array) {
            for (Object i : array) {
                values.add(create(String.valueOf(i)).rj);
            }
        }

        public IRabbitJson add(IRabbitJson value) {
            values.add(value);
            return this;
        }

        public IRabbitJson add(String key, IRabbitJson value) {
            return normalizedKey(key).equals(EMPTY_KEY) ? add(value) : null;
        }

        @Override
        public IRabbitJson clear() {
            values.clear();
            return this;
        }

        @Override
        public IRabbitJson removeKey(String key) {
            return normalizedKey(key).equals(EMPTY_KEY) ? clear() : this;
        }

        @Override
        public IRabbitJson removeValue(IRabbitJson value) {
            IRabbitJson valueToRemove = normalizeValue(value);
            if (this.equals(valueToRemove)) {
                clear();
            } else {
                if (value instanceof ValueRabbitJson) {
                    values.remove(valueToRemove);
                } else if (value instanceof ArrayRabbitJson) {
                    for (IRabbitJson v : ((ArrayRabbitJson) valueToRemove).values) {
                        removeValue(v);
                    }
                }
            }
            return this;
        }

        @Override
        public IRabbitJson remove(String key, IRabbitJson value) {
            return normalizedKey(key).equals(EMPTY_KEY) ? removeValue(value) : this;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            boolean comma = false;
            for (IRabbitJson a : values) {
                if (comma) {
                    sb.append(",");
                } else {
                    comma = true;
                }
                String quotes = a instanceof ValueRabbitJson ? "\"" : "";
                sb.append(quotes).append(a).append(quotes);
            }
            sb.append("]");
            return sb.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            return values.equals(((ArrayRabbitJson) o).values);
        }

        @Override
        public int hashCode() {
            return Objects.hash(values);
        }
    }
}
