package org.altart.telegrambridge.events;


import org.altart.telegrambridge.TelegramBridge;
import org.altart.telegrambridge.utils.Format;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;

public class GameEvent implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (TelegramBridge.config.getSendToTelegram() && TelegramBridge.config.getLogJoinAndLeaveEvent()) {
            String playerNick = event.getPlayer().getDisplayName();
            String message = Format.string(TelegramBridge.config.getMessagesFormatJoin(), "playername", playerNick);
            TelegramBridge.telegramBot.send(message);
            TelegramBridge.telegramBot.addPlayer(event.getPlayer().getDisplayName());
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        if (TelegramBridge.config.getSendToTelegram() && TelegramBridge.config.getLogJoinAndLeaveEvent()) {
            String playerNick = event.getPlayer().getDisplayName();
            String message = Format.string(TelegramBridge.config.getMessagesFormatLeave(), "playername", playerNick);
            TelegramBridge.telegramBot.send(message);
            TelegramBridge.telegramBot.removePlayer(event.getPlayer().getDisplayName());
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (TelegramBridge.config.getSendToTelegram() && TelegramBridge.config.getLogDeathEvent()) {
            String playerNick = event.getEntity().getDisplayName();
            String deathMessage = event.getDeathMessage();
            HashMap<String, String> values = new HashMap<>();
            values.put("playername", playerNick);
            values.put("deathmessage", deathMessage);
            String message = Format.string(TelegramBridge.config.getMessagesFormatDeath(), values);
            TelegramBridge.telegramBot.send(message);
        }
    }

    @EventHandler
    public void onPlayerAsleep(PlayerBedEnterEvent event) {
        if (TelegramBridge.config.getSendToTelegram() && TelegramBridge.config.getLogSleepEvent()) {
            String playerNick = event.getPlayer().getDisplayName();
            String message = Format.string(TelegramBridge.config.getMessagesFormatSleep(), "playername", playerNick);
            TelegramBridge.telegramBot.send(message);
        }
    }
}
