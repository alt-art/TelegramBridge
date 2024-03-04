package org.altart.telegrambridge;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Config {
    private final Plugin plugin;
    private static Config instance = null;

    private FileConfiguration config;

    public Config(Plugin plugin) {
        this.plugin = plugin;
        instance = this;
        load();
    }

    public static Config getInstance() {
        return instance;
    }

    public void load() {
        File configFile = new File(plugin.getDataFolder().getAbsolutePath() + "/config.yml");
        if (!configFile.exists()) {
            plugin.saveDefaultConfig();
        }
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    private void saveValue(String key, Object value) {
        File configFile = new File(plugin.getDataFolder().getAbsolutePath() + "/config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        config.set(key, value);
        try {
            config.save(configFile);
        } catch (Exception e) {
            System.out.println("Error saving config: " + e.getMessage());
        }
    }

    public void setThread(String chatId, Integer threadId) {
        List<Chat> chats = getChats();
        for (Chat chat : chats) {
            if (chat.id.equals(chatId)) {
                chat.thread = threadId;
                return;
            }
        }
        chats.add(new Chat(chatId, threadId));
        saveValue("chats", chats);
    }

    public String getBotToken() {
        return config.getString("bot_token");
    }

    public List<Chat> getChats() {
        return Chat.chatsFrom(config.getMapList("chats"));
    }

    public boolean getSendToTelegram() {
        return config.getBoolean("send_to_telegram");
    }

    public boolean getSendToChat() {
        return config.getBoolean("send_to_chat");
    }

    public boolean getLogJoinAndLeaveEvent() {
        return config.getBoolean("log_join_and_leave_event");
    }

    public boolean getLogDeathEvent() {
        return config.getBoolean("log_death_event");
    }

    public boolean getLogSleepEvent() {
        return config.getBoolean("log_sleep_event");
    }

    public String getMessagesFormatJoin() {
        return config.getString("messages.join");
    }

    public String getMessagesFormatLeave() {
        return config.getString("messages.leave");
    }

    public String getMessagesFormatDeath() {
        return config.getString("messages.death");
    }

    public String getMessagesFormatSleep() {
        return config.getString("messages.sleep");
    }

    public String getMessagesFormatTelegram() {
        return config.getString("messages.telegram");
    }

    public String getMessagesFormatMedia() {
        return config.getString("messages.media");
    }

    public String getMessagesFormatChat() {
        return config.getString("messages.chat");
    }

    public String getMessagesFormatReply() {
        return config.getString("messages.reply");
    }

    public String getMessagesFormatOnline() {
        return config.getString("messages.online");
    }

    public String getMessagesFormatTime() {
        return config.getString("messages.time");
    }

    public List<String> getMonths() {
        return config.getStringList("months");
    }

    public List<String> getMediaTypes() {
        return config.getStringList("media_types");
    }

    public static class Chat {
        public String id;

        @Nullable
        public Integer thread;

        public Chat(String id, @Nullable Integer thread) {
            this.id = id;
            this.thread = thread;
        }

        public static List<Chat> chatsFrom(List<Map<?, ?>> chats_map) {
            List<Chat> chats = new ArrayList<>();
            for (Map<?, ?> chat : chats_map) {
                String id = (String) chat.get("id");
                Integer thread = (Integer) chat.get("thread");
                chats.add(new Chat(id, thread));
            }
            return chats;
        }
    }
}
