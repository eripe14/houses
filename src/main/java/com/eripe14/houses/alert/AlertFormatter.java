package com.eripe14.houses.alert;

import com.eripe14.database.document.Document;
import panda.utilities.text.Formatter;

import java.util.LinkedHashMap;
import java.util.Map;

public class AlertFormatter implements Document {

    private final Map<String, String> placeholders;

    public AlertFormatter(Map<String, String> placeholders) {
        this.placeholders = placeholders;
    }

    public AlertFormatter() {
        this(new LinkedHashMap<>());
    }

    public String format(String message) {
        for (Map.Entry<String, String> entry : this.placeholders.entrySet()) {
            String key = entry.getKey();

            if (message.contains(key)) {
                message = message.replace(key, entry.getValue());
            }
        }

        return message;
    }

    public AlertFormatter register(String placeholder, String value) {
        this.placeholders.put(placeholder, value);
        return this;
    }

    public Map<String, String> getPlaceholders() {
        return placeholders;
    }

    public Formatter toFormatter() {
        Formatter formatter = new Formatter();
        this.placeholders.forEach(formatter::register);

        return formatter;
    }

    @Override
    public Class<? extends Document> getType() {
        return this.getClass();
    }
}