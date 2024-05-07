package org.altart.telegrambridge;

import org.altart.telegrambridge.bot.TelegramBot;
import org.altart.telegrambridge.commands.*;
import org.altart.telegrambridge.config.Config;
import org.altart.telegrambridge.config.Translations;
import org.altart.telegrambridge.database.SQLite;
import org.altart.telegrambridge.events.ChatEvent;
import org.altart.telegrambridge.events.GameEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.generics.BotSession;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.*;
import java.util.logging.Logger;

public final class TelegramBridge extends JavaPlugin {
    public static Plugin plugin;
    public static Logger log;
    public static Config config;
    public static Translations translations;
    public static SQLite database;

    public static TelegramBot telegramBot;
    private BotSession botSession;

    @Override
    public void onEnable() {
        plugin = this;
        log = getLogger();
        config = new Config();
        translations = new Translations(config.lang);
        database = new SQLite();

        if (Objects.equals(config.botToken, "YOUR_BOT_TOKEN") || Objects.equals(config.chats.get(0).id, "YOUR_CHAT_ID")) {
            log.severe("Please set your bot token and chat id in the config file");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        try {
            telegramBot = new TelegramBot(plugin);
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            botSession = telegramBotsApi.registerBot(telegramBot);
            log.info("Telegram bot registered");
        } catch (Exception e) {
            log.severe("Error registering bot: " + e.getMessage());
            Arrays.stream(e.getStackTrace()).forEach(line -> log.severe(line.toString()));
        }

        Bukkit.getPluginManager().registerEvents(new ChatEvent(), plugin);
        Bukkit.getPluginManager().registerEvents(new GameEvent(), plugin);
        try {
            Objects.requireNonNull(getCommand("tbreload")).setExecutor(new ReloadCommand());
            PluginCommand replyCommand = Objects.requireNonNull(getCommand("tbreply"));
            replyCommand.setExecutor(new ReplyCommand());
            replyCommand.setTabCompleter(new UserTabCompletion(2));
            PluginCommand mentionCommand = Objects.requireNonNull(getCommand("tbmention"));
            mentionCommand.setExecutor(new MentionCommand());
            mentionCommand.setTabCompleter(new UserTabCompletion(1));
            PluginCommand configCommand = Objects.requireNonNull(getCommand("tbconfig"));
            configCommand.setExecutor(new ConfigCommand());
            configCommand.setTabCompleter(new ConfigTabCompletion());
        } catch (NullPointerException e) {
            log.severe("Error registering command: " + e.getMessage());
            Arrays.stream(e.getStackTrace()).forEach(line -> TelegramBridge.log.severe(line.toString()));
        }
    }

    @Override
    public void onDisable() {
        database.close();
        if (botSession != null) {
            botSession.stop();
        }
    }
}
