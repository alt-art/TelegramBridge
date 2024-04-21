package org.altart.telegrambridge.events;


import net.md_5.bungee.chat.TranslationRegistry;
import org.altart.telegrambridge.TelegramBridge;
import org.altart.telegrambridge.utils.Format;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Objects;

public class GameEvent implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (TelegramBridge.config.sendToTelegram && TelegramBridge.config.joinAndLeaveEvent) {
            String playerNick = event.getPlayer().getDisplayName();
            String message = Format.string(TelegramBridge.translations.join, "playername", playerNick);
            TelegramBridge.telegramBot.broadcastMessage(message);
            TelegramBridge.telegramBot.pinMessageFeature.addPlayer(event.getPlayer().getDisplayName());
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        if (TelegramBridge.config.sendToTelegram && TelegramBridge.config.joinAndLeaveEvent) {
            String playerNick = event.getPlayer().getDisplayName();
            String message = Format.string(TelegramBridge.translations.leave, "playername", playerNick);
            TelegramBridge.telegramBot.broadcastMessage(message);
            TelegramBridge.telegramBot.pinMessageFeature.removePlayer(event.getPlayer().getDisplayName());
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (TelegramBridge.config.sendToTelegram && TelegramBridge.config.deathEvent) {
            String playerNick = event.getEntity().getDisplayName();
            String deathMessage = event.getDeathMessage();
            HashMap<String, String> values = new HashMap<>();
            values.put("playername", playerNick);
            values.put("deathmessage", deathMessage);
            String message = Format.string(TelegramBridge.translations.death, values);
            TelegramBridge.telegramBot.broadcastMessage(message);
        }
    }

    @EventHandler
    public void onPlayerAsleep(PlayerBedEnterEvent event) {
        if (TelegramBridge.config.sendToTelegram && TelegramBridge.config.sleepEvent) {
            String playerNick = event.getPlayer().getDisplayName();
            String message = Format.string(TelegramBridge.translations.sleep, "playername", playerNick);
            TelegramBridge.telegramBot.broadcastMessage(message);
        }
    }

    @EventHandler
    public void onAdvancement(PlayerAdvancementDoneEvent event) {
        if (TelegramBridge.config.sendToTelegram && TelegramBridge.config.advancementEvent) {
            String playerNick = event.getPlayer().getDisplayName();
            String advancementKey = event.getAdvancement().getKey().getKey().replace("/", ".");
            if (advancementKey.startsWith("recipes.")) {
                return;
            }
            String advancementText = TranslationRegistry.INSTANCE.translate("advancements." + advancementKey + ".title");
            if (advancementText == null) {
                advancementText = advancementKey;
            }
            String advancementDescription = TranslationRegistry.INSTANCE.translate("advancements." + advancementKey + ".description");
            if (advancementDescription != null) {
                advancementText += ": " + advancementDescription;
            }
            HashMap<String, String> values = new HashMap<>();
            values.put("playername", playerNick);
            values.put("advancement", advancementText);
            String message = Format.string(TelegramBridge.translations.advancement, values);
            TelegramBridge.telegramBot.broadcastMessage(message);
        }
    }
}
