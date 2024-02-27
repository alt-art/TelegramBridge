package org.altart.telegrambridge.utils;

import org.altart.telegrambridge.TelegramBridge;

import java.util.HashMap;
import java.util.Map;

public class Format {
    public static String string(String format, HashMap<String, String> values) {
        String result = format;
        result = result.replace("\\n", "\n");
        for (Map.Entry<String, String> entry : values.entrySet()) {
            result = result.replace("%" + entry.getKey() + "%", entry.getValue());
        }
        return result;
    }

    public static String string(String format, String key, String value) {
        String result = format;
        result = result.replace("\\n", "\n");
        result = result.replace("%" + key + "%", value);
        return result;
    }

    public static String chatMessage(String playerName, String message) {
        HashMap<String, String> values = new HashMap<>();
        values.put("playername", playerName);
        values.put("message", message);
        return string(TelegramBridge.config.messages_format_telegram, values);
    }
}
