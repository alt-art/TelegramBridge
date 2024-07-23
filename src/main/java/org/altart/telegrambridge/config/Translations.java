package org.altart.telegrambridge.config;

import org.altart.telegrambridge.TelegramBridge;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import javax.annotation.Nullable;
import java.io.*;
import java.lang.reflect.Field;
import java.util.*;

public class Translations {
    private final HashMap<String, Translation> translations = new HashMap<>();

    private final Set<String> loadedLanguages = new HashSet<>(Arrays.asList("en", "es", "jp", "pt", "ru"));

    private String defaultLang = "en";

    public Translations(@Nullable String defaultLang) {
        if (defaultLang != null) {
            this.defaultLang = defaultLang;
        }

        File translationsFolder = new File(TelegramBridge.plugin.getDataFolder(), "lang");
        if (translationsFolder.mkdirs()) {
            for (String lang : loadedLanguages) {
                TelegramBridge.plugin.saveResource("lang/" + lang + ".yml", false);
            }
        }
        File[] translationFiles = translationsFolder.listFiles();
        if (translationFiles != null) {
            for (File translationFile : translationFiles) {
                String lang = translationFile.getName().replace(".yml", "");
                loadedLanguages.add(lang);
                translations.put(lang, new Translation(translationFile));
            }
        }
    }

    public Translation get() {
        return get(defaultLang);
    }

    public Translation get(@Nullable String lang) {
        if (lang == null) {
            return get(defaultLang);
        }
        return translations.getOrDefault(lang, new Translation(null));
    }

    public void setDefaultLang(String lang) throws Exception {
        if (loadedLanguages.contains(lang)) {
            defaultLang = lang;
        } else {
            throw new Exception("Language not loaded: " + lang);
        }
    }

    public List<String> getLoadedLanguages() {
        return new ArrayList<>(loadedLanguages);
    }

    public static class Translation {
        public List<String> months;
        public List<String> mediaTypes;

        public String join;
        public String leave;
        public String death;
        public String advancement;
        public String sleep;
        public String serverStart;
        public String serverStop;

        public String chatMessage;
        public String telegramMessage;
        public String telegramMedia;
        public String telegramReply;

        public String online;
        public String time;

        public String replyButton;
        public String replyHint;

        public Translation(@Nullable File translationsFile) {
            if (translationsFile != null && translationsFile.exists()) {
                FileConfiguration translations = YamlConfiguration.loadConfiguration(translationsFile);
                Field[] fields = this.getClass().getDeclaredFields();
                for (Field field : fields) {
                    try {
                        if (translations.contains(field.getName()) && field.getType().equals(String.class)) {
                            field.set(this, translations.get(field.getName()));
                        }
                        if (translations.contains(field.getName()) && field.getType().equals(List.class)) {
                            field.set(this, translations.getStringList(field.getName()));
                        }
                    } catch (Exception e) {
                        TelegramBridge.log.severe("Error loading translations: " + e.getMessage());
                        Arrays.stream(e.getStackTrace()).forEach(line -> TelegramBridge.log.severe(line.toString()));
                    }
                }
            }
        }
    }
}
