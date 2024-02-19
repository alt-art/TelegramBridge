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
        if (TelegramBridge.config.send_to_telegram && TelegramBridge.config.log_join_and_leave_event) {
            String playerNick = event.getPlayer().getDisplayName();
            String message = Format.string(TelegramBridge.config.messages_format_join, "playername", playerNick);
            TelegramBridge.sendTelegramMessage(message);
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        if (TelegramBridge.config.send_to_telegram && TelegramBridge.config.log_join_and_leave_event) {
            String playerNick = event.getPlayer().getDisplayName();
            String message = Format.string(TelegramBridge.config.messages_format_leave, "playername", playerNick);
            TelegramBridge.sendTelegramMessage(message);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (TelegramBridge.config.send_to_telegram && TelegramBridge.config.log_death_event) {
            String playerNick = event.getEntity().getDisplayName();
            String deathMessage = event.getDeathMessage();
            HashMap<String, String> values = new HashMap<>();
            values.put("playername", playerNick);
            values.put("deathmessage", deathMessage);
            String message = Format.string(TelegramBridge.config.messages_format_death, values);
            TelegramBridge.sendTelegramMessage(message);
        }
    }

    @EventHandler
    public void onPlayerAsleep(PlayerBedEnterEvent event) {
        if (TelegramBridge.config.send_to_telegram && TelegramBridge.config.log_sleep_event) {
            String playerNick = event.getPlayer().getDisplayName();
            String message = Format.string(TelegramBridge.config.messages_format_sleep, "playername", playerNick);
            TelegramBridge.sendTelegramMessage(message);
        }
    }
}
