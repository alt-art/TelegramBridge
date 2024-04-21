package org.altart.telegrambridge.config;

import org.altart.telegrambridge.TelegramBridge;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.lang.reflect.Field;
import java.util.*;

@SuppressWarnings("unused")
public class Config {
    public String botToken = "YOUR_BOT_TOKEN";
    public List<Chat> chats = Collections.singletonList(new Chat("YOUR_CHAT_ID", null, null));

    public boolean sendToChat = true;
    public boolean sendToTelegram = true;

    public boolean joinAndLeaveEvent = true;
    public boolean deathEvent = true;
    public boolean advancementEvent = true;
    public boolean sleepEvent = false;

    public String pinned = "Hey welcome to the chat!\nThere are %count% players online%players%";

    public Config() {
        load();
    }

    public void load() {
        File configFile = new File(TelegramBridge.plugin.getDataFolder().getAbsoluteFile(), "config.yml");
        if (configFile.exists()) {
            FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
            Field[] fields = this.getClass().getDeclaredFields();
            for (Field field : fields) {
                try {
                    if (config.contains(field.getName())) {
                        if (field.getType().equals(String.class)) {
                            field.set(this, config.get(field.getName()));
                        } else if (field.getType().equals(Boolean.class)) {
                            field.set(this, config.getBoolean(field.getName()));
                        } else if (field.getType().equals(List.class)) {
                            field.set(this, Chat.chatsFrom(config.getMapList(field.getName())));
                        }
                    }
                } catch (Exception e) {
                    TelegramBridge.log.severe("Error loading config: " + e.getMessage());
                    Arrays.stream(e.getStackTrace()).forEach(line -> TelegramBridge.log.severe(line.toString()));
                }
            }
        } else {
            TelegramBridge.log.warning("Config file not found making a new one at %s" + configFile.getAbsolutePath());
            try {
                FileConfiguration config = new YamlConfiguration();
                config.set("botToken", botToken);
                config.set("chats", Chat.chatsToMaps(chats));
                config.save(configFile);
                TelegramBridge.log.info("Config file created at " + configFile.getAbsolutePath());
            } catch (Exception e) {
                TelegramBridge.log.severe("Error creating config file: " + e.getMessage());
                Arrays.stream(e.getStackTrace()).forEach(line -> TelegramBridge.log.severe(line.toString()));
            }
        }
    }

    public void save() {
        File configFile = new File(TelegramBridge.plugin.getDataFolder().getAbsoluteFile(), "config.yml");
        try {
            FileConfiguration config = new YamlConfiguration();
            config.set("botToken", botToken);
            config.set("chats", Chat.chatsToMaps(chats));
            config.save(configFile);
        } catch (Exception e) {
            TelegramBridge.log.severe("Error saving config: " + e.getMessage());
            Arrays.stream(e.getStackTrace()).forEach(line -> TelegramBridge.log.severe(line.toString()));
        }
    }

    public void setPinnedMessageId(@NotNull String chatId, @Nullable Integer messageId) {
        for (Chat chat : chats) {
            if (chat.id.equals(chatId)) {
                chat.pinnedMessageId = messageId;
                break;
            }
        }
        save();
    }

    public void setThread(@NotNull String chatId, @Nullable Integer threadId) {
        for (Chat chat : chats) {
            if (chat.id.equals(chatId)) {
                chat.thread = threadId;
                break;
            }
        }
        save();
    }

    public static class Chat {
        public String id;

        @Nullable
        public Integer thread;

        @Nullable
        public Integer pinnedMessageId;

        public Chat(@NotNull String id, @Nullable Integer thread, @Nullable Integer pinnedMessageId) {
            this.id = id;
            this.thread = thread;
            this.pinnedMessageId = pinnedMessageId;
        }

        public static List<Chat> chatsFrom(List<Map<?, ?>> chats_map) {
            List<Chat> chats = new ArrayList<>();
            for (Map<?, ?> chat : chats_map) {
                String id = (String) chat.get("id");
                Integer thread = null;
                if (chat.get("thread") != null) {
                    thread = (int) chat.get("thread");
                }
                Integer pinnedMessageId = null;
                if (chat.get("pinnedMessageId") != null) {
                    pinnedMessageId = (int) chat.get("pinnedMessageId");
                }
                chats.add(new Chat(id, thread, pinnedMessageId));
            }
            return chats;
        }

        public static List<Map<String, Object>> chatsToMaps(List<Chat> chats) {
            List<Map<String, Object>> chats_map = new ArrayList<>();
            for (Chat chat : chats) {
                Map<String, Object> chat_map = new HashMap<>();
                chat_map.put("id", chat.id);
                chat_map.put("thread", chat.thread);
                chat_map.put("pinnedMessageId", chat.pinnedMessageId);
                chats_map.add(chat_map);
            }
            return chats_map;
        }
    }
}
