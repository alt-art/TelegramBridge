package org.altart.telegrambridge.events;

import org.altart.telegrambridge.Permissions;
import org.altart.telegrambridge.TelegramBridge;
import org.altart.telegrambridge.utils.Format;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;

public class ChatEvent implements Listener {
    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (!event.getMessage().startsWith("/") && player.hasPermission(Permissions.SEND.getString()) && TelegramBridge.config.send_to_telegram) {
            String playerNick = player.getDisplayName();
            String message = event.getMessage().replaceAll("§.", "");
            if (!message.isEmpty()) {
                HashMap<String, String> values = new HashMap<>();
                values.put("playername", playerNick);
                values.put("message", message);
                String text = Format.string(TelegramBridge.config.messages_format_telegram, values);
                TelegramBridge.telegramBot.send(text);
            }
        }
    }
}
