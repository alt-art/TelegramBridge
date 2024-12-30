package org.altart.telegrambridge.auth;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerSnapshot {

    private final ItemStack[] inventory;
    private final ItemStack[] armor;
    private final ItemStack[] extras;
    private final Collection<PotionEffect> effects;
    private final GameMode gameMode;

    private static final Map<UUID, PlayerSnapshot> SNAPSHOTS = new HashMap<>();

    PlayerSnapshot(@NotNull Player player) {
        this.inventory = player.getInventory().getContents();
        this.armor = player.getInventory().getArmorContents();
        this.extras = player.getInventory().getExtraContents();
        this.effects = player.getActivePotionEffects();
        this.gameMode = player.getGameMode();
    }

    public static void save(@NotNull Player player) {
        SNAPSHOTS.put(player.getUniqueId(), new PlayerSnapshot(player));
    }

    public static void decline(@NotNull Player player) {
        player.setGameMode(GameMode.CREATIVE);
        player.getInventory().clear();
        player.setAllowFlight(true);
        player.setFlying(true);
        player.setGliding(false);
        player.setSneaking(false);
        player.setSprinting(false);
        player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
    }

    public static void restore(@NotNull Player player) {
        PlayerSnapshot snapshot = SNAPSHOTS.remove(player.getUniqueId());
        if (snapshot == null) return;

        player.setGameMode(snapshot.getGameMode());

        if (player.getGameMode() == GameMode.SURVIVAL) {
            player.setAllowFlight(false);
            player.setFlying(false);
        }

        player.getActivePotionEffects().stream().map(PotionEffect::getType).forEach(player::removePotionEffect);
        player.addPotionEffects(snapshot.getPotionEffects());

        player.getInventory().setContents(snapshot.getInventory());
        player.getInventory().setArmorContents(snapshot.getArmor());
        player.getInventory().setExtraContents(snapshot.getExtras());
    }

    @NotNull
    private ItemStack[] getInventory() {
        return this.inventory;
    }

    private ItemStack[] getArmor() {
        return this.armor;
    }

    private ItemStack[] getExtras() {
        return extras;
    }

    @NotNull
    private Collection<PotionEffect> getPotionEffects() {
        return this.effects;
    }

    @NotNull
    private GameMode getGameMode() {
        return this.gameMode;
    }
}
