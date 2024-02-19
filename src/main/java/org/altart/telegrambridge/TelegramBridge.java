package org.altart.telegrambridge;

import org.altart.telegrambridge.commands.ReloadCommand;
import org.altart.telegrambridge.events.ChatEvent;
import org.altart.telegrambridge.events.GameEvent;
import org.altart.telegrambridge.utils.Format;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.generics.BotSession;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.HashMap;
import java.util.Objects;
import java.util.logging.Logger;

public final class TelegramBridge extends JavaPlugin {
    public static Logger log;
    public static Config config;

    private static BotMessageHandler botMessageHandler;
    private BotSession botSession;

    @Override
    public void onEnable() {
        try {
            log = getLogger();
            config = new Config(this);
            if (Objects.equals(config.bot_token, "your_token")) {
                log.severe("Please set your bot token in the config file!");
                return;
            }
            botMessageHandler = new BotMessageHandler(this);
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            botSession = telegramBotsApi.registerBot(botMessageHandler);
            log.info("Bot registered!");
            botMessageHandler.setOnMessageCallback(message -> {
                if (!config.send_to_chat) return;
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.hasPermission(Permissions.RECEIVE.getString())) {
                        HashMap<String, String> values = new HashMap<>();
                        values.put("playername", message.getFrom().getFirstName());
                        values.put("message", message.getText());
                        String text = Format.string(config.messages_format_chat, values);
                        log.info(text);
                        player.sendMessage(text);
                    }
                }
            });
            Bukkit.getPluginManager().registerEvents(new ChatEvent(), this);
            Bukkit.getPluginManager().registerEvents(new GameEvent(), this);
            try {
                Objects.requireNonNull(getCommand("tbreload")).setExecutor(new ReloadCommand());
            } catch (NullPointerException e) {
                log.severe("Error registering command: " + e.getMessage());
            }
        } catch (Exception e) {
            log.severe("Error: " + e.getMessage());
        }
    }

    @Override
    public void onDisable() {
        botSession.stop();
    }

    public static void sendTelegramMessage(String message) {
        if (botMessageHandler == null) {
            return;
        }
        botMessageHandler.broadcastMessage(message);
    }
}
