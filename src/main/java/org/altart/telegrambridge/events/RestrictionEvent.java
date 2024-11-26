package org.altart.telegrambridge.events;

import org.altart.telegrambridge.AuthManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;

import java.util.UUID;

public class RestrictionEvent implements Listener {
    private final AuthManager authManager;

    public RestrictionEvent(AuthManager authManager) {
        this.authManager = authManager;
    }

    @EventHandler(ignoreCancelled = true)
    public void onLoginBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        event.setCancelled(!authManager.isLogged(player));
    }

    @EventHandler(ignoreCancelled = true)
    public void onLoginBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        event.setCancelled(!authManager.isLogged(player));
    }

    @EventHandler(ignoreCancelled = true)
    public void onLoginDisplayCommands(PlayerCommandSendEvent event) {
        Player player = event.getPlayer();
        if (!authManager.isLogged(player)) {
            event.getCommands().clear();
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onLoginCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        if (!authManager.isLogged(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onLoginInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (authManager.isLogged(player)) return;

        event.setUseInteractedBlock(Event.Result.DENY);
        event.setUseItemInHand(Event.Result.DENY);
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onLoginInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        event.setCancelled(!authManager.isLogged(player));
    }

    @EventHandler(ignoreCancelled = true)
    public void onLoginInteractEntity(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();
        event.setCancelled(!authManager.isLogged(player));
    }

    @EventHandler(ignoreCancelled = true)
    public void onLoginInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        event.setCancelled(!authManager.isLogged(player));
    }

    @EventHandler(ignoreCancelled = true)
    public void onLoginInventorySwap(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        event.setCancelled(!authManager.isLogged(player));
    }

    @EventHandler(ignoreCancelled = true)
    public void onLoginInventoryOpen(InventoryOpenEvent event) {
        Player player = (Player) event.getPlayer();
        event.setCancelled(!authManager.isLogged(player));
    }

    @EventHandler(ignoreCancelled = true)
    public void onLoginItemDamage(PlayerItemDamageEvent event) {
        Player player = event.getPlayer();
        event.setCancelled(!authManager.isLogged(player));
    }

    @EventHandler(ignoreCancelled = true)
    public void onLoginItemPickup(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        event.setCancelled(!authManager.isLogged(player));
    }

    @EventHandler(ignoreCancelled = true)
    public void onLoginItemDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        event.setCancelled(!authManager.isLogged(player));
    }

    @EventHandler(ignoreCancelled = true)
    public void onLoginDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            event.setCancelled(!authManager.isLogged(player));
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onLoginDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            event.setCancelled(!authManager.isLogged(player));
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onLoginDamage(EntityDamageByBlockEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            event.setCancelled(!authManager.isLogged(player));
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onLoginMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (authManager.isLogged(player)) return;

        Location to = event.getTo();
        if (to == null) {
            event.setCancelled(true);
            return;
        }

        Location from = event.getFrom();

        if (from.getX() != to.getX() || from.getY() != to.getY() || from.getZ() != to.getZ()) {
            event.setCancelled(true);
        }
    }
}
