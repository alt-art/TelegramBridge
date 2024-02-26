package org.altart.telegrambridge;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Config {
    public String bot_token = null;
    public List<Chats> chats = null;
    public boolean send_to_telegram = true;
    public boolean send_to_chat = true;
    public boolean log_join_and_leave_event = true;
    public boolean log_death_event = true;
    public boolean log_sleep_event = false;
    public String messages_format_join = null;
    public String messages_format_leave = null;
    public String messages_format_death = null;
    public String messages_format_sleep = null;
    public String messages_format_telegram = null;
    public String messages_format_chat = null;
    public String messages_format_reply = null;
    public String messages_format_online = null;
    public String messages_format_time = null;
    public List<String> months = null;

    private final Plugin plugin;
    private static Config instance = null;

    public Config(Plugin plugin) {
        this.plugin = plugin;
        instance = this;
        load();
    }

    public void load() {
        File configFile = new File(plugin.getDataFolder().getAbsolutePath() + "/config.yml");
        if (!configFile.exists()) {
            plugin.saveDefaultConfig();
        }
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        bot_token = config.getString("bot_token");
        chats = getChats(config.getMapList("chats"));
        send_to_telegram = config.getBoolean("send_to_telegram");
        send_to_chat = config.getBoolean("send_to_chat");
        log_join_and_leave_event = config.getBoolean("log_join_and_leave_event");
        log_death_event = config.getBoolean("log_death_event");
        log_sleep_event = config.getBoolean("log_sleep_event");
        messages_format_join = config.getString("messages.join");
        messages_format_leave = config.getString("messages.leave");
        messages_format_death = config.getString("messages.death");
        messages_format_sleep = config.getString("messages.sleep");
        messages_format_telegram = config.getString("messages.telegram");
        messages_format_chat = config.getString("messages.chat");
        messages_format_reply = config.getString("messages.reply");
        messages_format_online = config.getString("messages.online");
        messages_format_time = config.getString("messages.time");
        months = config.getStringList("months");
    }

    private List<Chats> getChats(List<Map<?, ?>> chats_map) {
        List<Chats> chats = new ArrayList<>();
        for (Map<?, ?> chat : chats_map) {
            String id = (String) chat.get("id");
            Integer thread = (Integer) chat.get("thread");
            chats.add(new Chats(id, thread));
        }
        return chats;
    }

    public void setThread(String chatId, Integer threadId) {
        for (Chats chat : chats) {
            if (chat.id.equals(chatId)) {
                chat.thread = threadId;
                return;
            }
        }
        chats.add(new Chats(chatId, threadId));
        save();
    }

    public static Config getInstance() {
        return instance;
    }

    private void save() {
        File configFile = new File(plugin.getDataFolder().getAbsolutePath() + "/config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        config.set("bot_token", bot_token);
        List<Map<String, Object>> chats_map = new ArrayList<>();
        for (Chats chat : chats) {
            Map<String, Object> chat_map = new HashMap<>();
            chat_map.put("id", chat.id);
            chat_map.put("thread", chat.thread);
            chats_map.add(chat_map);
        }
        config.set("chats", chats_map);
        config.set("send_to_telegram", send_to_telegram);
        config.set("send_to_chat", send_to_chat);
        config.set("log_join_and_leave_event", log_join_and_leave_event);
        config.set("log_death_event", log_death_event);
        config.set("log_sleep_event", log_sleep_event);
        config.set("messages.join", messages_format_join);
        config.set("messages.leave", messages_format_leave);
        config.set("messages.death", messages_format_death);
        config.set("messages.sleep", messages_format_sleep);
        config.set("messages.telegram", messages_format_telegram);
        config.set("messages.chat", messages_format_chat);
        config.set("messages.reply", messages_format_reply);
        config.set("messages.online", messages_format_online);
        config.set("messages.time", messages_format_time);
        config.set("months", months);
        try {
            config.save(configFile);
        } catch (Exception e) {
            System.out.println("Error saving config: " + e.getMessage());
        }
    }

    public static class Chats {
        public String id;

        @Nullable
        public Integer thread;

        public Chats(String id, @Nullable Integer thread) {
            this.id = id;
            this.thread = thread;
        }
    }
}
