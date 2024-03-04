package org.altart.telegrambridge;

import org.altart.telegrambridge.commands.MentionCommand;
import org.altart.telegrambridge.commands.MentionTabCompletion;
import org.altart.telegrambridge.commands.ReloadCommand;
import org.altart.telegrambridge.commands.ReplyCommand;
import org.altart.telegrambridge.events.ChatEvent;
import org.altart.telegrambridge.events.GameEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.logging.Logger;

public final class TelegramBridge extends JavaPlugin {
    public static Logger log;
    public static Config config;

    public static TelegramBot telegramBot;

    @Override
    public void onEnable() {
        log = getLogger();
        config = new Config(this);
        if (Objects.equals(config.getBotToken(), "your_token")) {
            log.severe("Please set your bot token in the config file!");
            return;
        }
        telegramBot = new TelegramBot(this);

        Bukkit.getPluginManager().registerEvents(new ChatEvent(), this);
        Bukkit.getPluginManager().registerEvents(new GameEvent(), this);
        try {
            Objects.requireNonNull(getCommand("tbreload")).setExecutor(new ReloadCommand());
            Objects.requireNonNull(getCommand("tbreply")).setExecutor(new ReplyCommand());
            PluginCommand markCommand = Objects.requireNonNull(getCommand("tbmention"));
            markCommand.setExecutor(new MentionCommand());
            markCommand.setTabCompleter(new MentionTabCompletion());
        } catch (NullPointerException e) {
            log.severe("Error registering command: " + e.getMessage());
        }
    }

    @Override
    public void onDisable() {
        telegramBot.stop();
    }
}
