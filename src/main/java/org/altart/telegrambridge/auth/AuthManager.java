package org.altart.telegrambridge.auth;

import org.altart.telegrambridge.TelegramBridge;
import org.altart.telegrambridge.utils.Format;
import org.apache.commons.lang3.RandomStringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class AuthManager {
    private final Player player;
    private final Date sessionStart;
    private boolean logged;

    public static final HashMap<UUID, AuthManager> AUTHS = new HashMap<>();
    private static final HashMap<String, UUID> CODES = new HashMap<>();

    public AuthManager(Player player) {
        this.player = player;
        this.sessionStart = new Date();
        this.logged = false;
        Bukkit.getScheduler().runTask(
            TelegramBridge.plugin, () -> {
                PlayerSnapshot.save(player);
                PlayerSnapshot.decline(player);
            }
        );
    }

    public static void addSession(Player player) {
        UUID uuid = player.getUniqueId();
        AUTHS.put(uuid, new AuthManager(player));
        String code = RandomStringUtils.randomAlphanumeric(6).toLowerCase();
        CODES.put(code, uuid);
        player.sendMessage("Type the following code in the Telegram chat: link-" + code);
    }

    public static boolean isLogged(Player player) {
        UUID uuid = player.getUniqueId();
        if (AUTHS.containsKey(uuid)) {
            AuthManager authManager = AUTHS.get(uuid);
            return authManager.logged;
        }
        return false;
    }

    public static void checkExpired() {
        AUTHS.entrySet().removeIf(entry -> {
            AuthManager authManager = entry.getValue();
            if (authManager.isExpired()) {
                Bukkit.getScheduler().runTask(
                    TelegramBridge.plugin, () -> {
                        Player player = authManager.player;
                        PlayerSnapshot.restore(player);
                        player.kickPlayer("Telegram authentication session expired");
                    });
                return true;
            }
            return false;
        });
    }

    public static void loginTitle() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            AuthManager authManager = AUTHS.get(player.getUniqueId());
            if (!authManager.logged) {
                CODES.entrySet()
                    .stream()
                    .filter(entry -> entry.getValue().equals(player.getUniqueId()))
                    .findFirst().map(Map.Entry::getKey)
                    .ifPresent(code ->
                        player.sendTitle(
                            ChatColor.UNDERLINE + "" + ChatColor.ITALIC + ChatColor.YELLOW + "AUTHENTICATION",
                            "Type link-" + code + " in the Telegram chat", 0, 40, 0
                        )
                    );
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 1));
            }
        }
    }

    public static void login(String sessionCode, Long telegramId) {
        UUID uuid = CODES.get(sessionCode);
        if (AUTHS.containsKey(uuid)) {
            AuthManager authManager = AUTHS.get(uuid);
            Player player = authManager.player;
            Long associatedTelegramId = TelegramBridge.database.getTelegramId(player.getName());
            if (associatedTelegramId != null) {
                if (associatedTelegramId.equals(telegramId)) {
                    player.sendTitle(
                        ChatColor.UNDERLINE + "" + ChatColor.ITALIC + ChatColor.GREEN + "AUTHENTICATED", "", 10, 70,
                        20);
                    authManager.login();
                }
            } else {
                TelegramBridge.database.linkAccount(player.getName(), telegramId);
                player.sendTitle(
                    ChatColor.UNDERLINE + "" + ChatColor.ITALIC + ChatColor.GREEN + "ACCOUNT LINKED", "", 10, 70,
                    20);
                authManager.login();
            }
        }
    }

    public static void logout(Player player) {
        Bukkit.getScheduler().runTask(TelegramBridge.plugin, () -> PlayerSnapshot.restore(player));
        AUTHS.remove(player.getUniqueId());
        CODES.entrySet().removeIf(entry -> entry.getValue().equals(player.getUniqueId()));
    }

    private boolean isExpired() {
        return (new Date().getTime() - sessionStart.getTime() > 60000) && !logged;
    }

    public void login() {
        CODES.entrySet().removeIf(entry -> entry.getValue().equals(this.player.getUniqueId()));
        Bukkit.getScheduler().runTask(TelegramBridge.plugin, () -> PlayerSnapshot.restore(this.player));
        if (TelegramBridge.config.sendToTelegram && TelegramBridge.config.joinAndLeaveEvent) {
            String playerNick = this.player.getDisplayName();
            String message = Format.string(TelegramBridge.translations.get().join, "playername", playerNick);
            TelegramBridge.telegramBot.broadcastMessage(message);
            TelegramBridge.telegramBot.pinMessageFeature.addPlayer(playerNick);
        }
        this.logged = true;
    }
}
