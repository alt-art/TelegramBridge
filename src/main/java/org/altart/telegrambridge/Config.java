package org.altart.telegrambridge;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.List;

public class Config {
    public String bot_token = null;
    public List<String> chat_ids = null;
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
        chat_ids = config.getStringList("chat_ids");
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
        messages_format_online = config.getString("messages.online");
        messages_format_time = config.getString("messages.time");
        months = config.getStringList("months");
    }

    public static Config getInstance() {
        return instance;
    }
}
