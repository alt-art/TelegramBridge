package org.altart.telegrambridge.utils;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class Format {
    public static String string(@NotNull String format, @NotNull HashMap<String, String> values) {
        String result = format;
        result = result.replace("\\n", "\n");
        for (Map.Entry<String, String> entry : values.entrySet()) {
            String value = entry.getValue();
            if (value == null) {
                value = "";
            }
            result = result.replace("%" + entry.getKey() + "%", value);
        }
        return result;
    }

    public static String string(@NotNull String format, @NotNull String key, @NotNull String value) {
        String result = format;
        result = result.replace("\\n", "\n");
        result = result.replace("%" + key + "%", value);
        return result;
    }
}
