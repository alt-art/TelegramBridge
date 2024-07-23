package org.altart.telegrambridge.events;

import org.altart.telegrambridge.StandardMockTest;
import org.altart.telegrambridge.TelegramBridge;
import org.altart.telegrambridge.bot.TelegramBot;
import org.altart.telegrambridge.bot.feature.PinMessage;
import org.altart.telegrambridge.config.Config;
import org.altart.telegrambridge.config.Translations;
import org.bukkit.*;
import org.bukkit.advancement.Advancement;
import org.bukkit.block.*;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.io.File;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

class GameEventTest extends StandardMockTest {
    @Mock
    Plugin mockPlugin;

    @Mock
    Config mockConfig;

    @Mock
    Player mockPlayer;

    @Mock
    TelegramBot mockTelegramBot;

    @Mock
    PinMessage mockPinMessage;

    final GameEvent gameEvent = new GameEvent();

    @Test
    @DisplayName("Test onPlayerJoin() method")
    void onPlayerJoin() {
        File translationsFolder = new File("src/test/resources");
        when(mockPlugin.getDataFolder()).thenReturn(translationsFolder);

        TelegramBridge.config = mockConfig;
        TelegramBridge.config.sendToTelegram = true;
        TelegramBridge.config.joinAndLeaveEvent = true;
        TelegramBridge.plugin = mockPlugin;
        TelegramBridge.translations = new Translations(null);
        TelegramBridge.telegramBot = mockTelegramBot;
        TelegramBridge.telegramBot.pinMessageFeature = mockPinMessage;

        when(mockPlayer.getDisplayName()).thenReturn("playerNick");

        PlayerJoinEvent event = new PlayerJoinEvent(mockPlayer, "Player joined the game");
        gameEvent.onPlayerJoin(event);

        verify(mockTelegramBot, times(1)).broadcastMessage("Player playerNick joined the game!");
        verify(mockPinMessage, times(1)).addPlayer("playerNick");
        verify(mockPlayer, times(1)).getDisplayName();
    }

    @Test
    @DisplayName("Test onPlayerJoin() method with joinAndLeaveEvent disabled")
    void onPlayerJoinNoEvent() {
        TelegramBridge.config = mockConfig;
        TelegramBridge.config.sendToTelegram = true;
        TelegramBridge.config.joinAndLeaveEvent = false;

        PlayerJoinEvent event = new PlayerJoinEvent(mockPlayer, "Player joined the game");
        gameEvent.onPlayerJoin(event);

        verify(mockTelegramBot, never()).broadcastMessage(anyString());
        verify(mockPinMessage, never()).addPlayer(anyString());
        verify(mockPlayer, never()).getDisplayName();
    }

    @Test
    @DisplayName("Test onPlayerJoin() method with sendToTelegram disabled")
    void onPlayerJoinNoTelegram() {
        TelegramBridge.config = mockConfig;
        TelegramBridge.config.sendToTelegram = false;
        TelegramBridge.config.joinAndLeaveEvent = true;

        PlayerJoinEvent event = new PlayerJoinEvent(mockPlayer, "Player joined the game");
        gameEvent.onPlayerJoin(event);

        verify(mockTelegramBot, never()).broadcastMessage(anyString());
        verify(mockPinMessage, never()).addPlayer(anyString());
        verify(mockPlayer, never()).getDisplayName();
    }

    @Test
    @DisplayName("Test onPlayerLeave() method")
    void onPlayerLeave() {
        File translationsFolder = new File("src/test/resources");
        when(mockPlugin.getDataFolder()).thenReturn(translationsFolder);

        TelegramBridge.config = mockConfig;
        TelegramBridge.config.sendToTelegram = true;
        TelegramBridge.config.joinAndLeaveEvent = true;
        TelegramBridge.plugin = mockPlugin;
        TelegramBridge.translations = new Translations(null);
        TelegramBridge.telegramBot = mockTelegramBot;
        TelegramBridge.telegramBot.pinMessageFeature = mockPinMessage;

        when(mockPlayer.getDisplayName()).thenReturn("playerNick");

        PlayerQuitEvent leaveEvent = new PlayerQuitEvent(mockPlayer, "Player left the game");
        gameEvent.onPlayerLeave(leaveEvent);

        verify(mockTelegramBot, times(1)).broadcastMessage("Player playerNick left the game!");
        verify(mockPinMessage, times(1)).removePlayer("playerNick");
        verify(mockPlayer, times(1)).getDisplayName();
    }

    @Test
    @DisplayName("Test onPlayerLeave() method with joinAndLeaveEvent disabled")
    void onPlayerLeaveNoEvent() {
        TelegramBridge.config = mockConfig;
        TelegramBridge.config.sendToTelegram = true;
        TelegramBridge.config.joinAndLeaveEvent = false;

        PlayerQuitEvent leaveEvent = new PlayerQuitEvent(mockPlayer, "Player left the game");
        gameEvent.onPlayerLeave(leaveEvent);

        verify(mockTelegramBot, never()).broadcastMessage(anyString());
        verify(mockPinMessage, never()).removePlayer(anyString());
        verify(mockPlayer, never()).getDisplayName();
    }

    @Test
    @DisplayName("Test onPlayerLeave() method with sendToTelegram disabled")
    void onPlayerLeaveNoTelegram() {
        TelegramBridge.config = mockConfig;
        TelegramBridge.config.sendToTelegram = false;
        TelegramBridge.config.joinAndLeaveEvent = true;

        PlayerQuitEvent leaveEvent = new PlayerQuitEvent(mockPlayer, "Player left the game");
        gameEvent.onPlayerLeave(leaveEvent);

        verify(mockTelegramBot, never()).broadcastMessage(anyString());
        verify(mockPinMessage, never()).removePlayer(anyString());
        verify(mockPlayer, never()).getDisplayName();
    }

    @Test
    @DisplayName("Test onPlayerDeath() method")
    void onPlayerDeath() {
        File translationsFolder = new File("src/test/resources");
        when(mockPlugin.getDataFolder()).thenReturn(translationsFolder);

        TelegramBridge.config = mockConfig;
        TelegramBridge.config.sendToTelegram = true;
        TelegramBridge.config.deathEvent = true;
        TelegramBridge.plugin = mockPlugin;
        TelegramBridge.translations = new Translations(null);
        TelegramBridge.telegramBot = mockTelegramBot;

        when(mockPlayer.getDisplayName()).thenReturn("playerNick");
        when(mockPlayer.isDead()).thenReturn(true);

        List<ItemStack> items = Collections.singletonList(new ItemStack(Material.STICK));
        PlayerDeathEvent deathEvent = new PlayerDeathEvent(mockPlayer, items, 0, "Player died");

        gameEvent.onPlayerDeath(deathEvent);

        verify(mockTelegramBot, times(1)).broadcastMessage("Player playerNick died! Player died");
        verify(mockPlayer, times(1)).getDisplayName();
    }

    @Test
    @DisplayName("Test onPlayerDeath() method with deathEvent disabled")
    void onPlayerDeathNoEvent() {
        TelegramBridge.config = mockConfig;
        TelegramBridge.config.sendToTelegram = true;
        TelegramBridge.config.deathEvent = false;

        List<ItemStack> items = Collections.singletonList(new ItemStack(Material.STICK));
        PlayerDeathEvent deathEvent = new PlayerDeathEvent(mockPlayer, items, 0, "Player died");
        gameEvent.onPlayerDeath(deathEvent);

        verify(mockTelegramBot, never()).broadcastMessage(anyString());
        verify(mockPlayer, never()).getDisplayName();
    }

    @Test
    @DisplayName("Test onPlayerDeath() method with sendToTelegram disabled")
    void onPlayerDeathNoTelegram() {
        TelegramBridge.config = mockConfig;
        TelegramBridge.config.sendToTelegram = false;
        TelegramBridge.config.deathEvent = true;

        List<ItemStack> items = Collections.singletonList(new ItemStack(Material.STICK));
        PlayerDeathEvent deathEvent = new PlayerDeathEvent(mockPlayer, items, 0, "Player died");
        gameEvent.onPlayerDeath(deathEvent);

        verify(mockTelegramBot, never()).broadcastMessage(anyString());
        verify(mockPlayer, never()).getDisplayName();
    }

    @Test
    @DisplayName("Test onPlayerAsleep() method")
    void onPlayerAsleep() {
        File translationsFolder = new File("src/test/resources");
        when(mockPlugin.getDataFolder()).thenReturn(translationsFolder);

        TelegramBridge.config = mockConfig;
        TelegramBridge.config.sendToTelegram = true;
        TelegramBridge.config.sleepEvent = true;
        TelegramBridge.plugin = mockPlugin;
        TelegramBridge.translations = new Translations(null);
        TelegramBridge.telegramBot = mockTelegramBot;

        when(mockPlayer.getDisplayName()).thenReturn("playerNick");

        PlayerBedEnterEvent.BedEnterResult bedEnterResult = PlayerBedEnterEvent.BedEnterResult.OK;
        PlayerBedEnterEvent sleepEvent = new PlayerBedEnterEvent(mockPlayer, mock(Block.class), bedEnterResult);
        gameEvent.onPlayerAsleep(sleepEvent);

        verify(mockTelegramBot, times(1)).broadcastMessage("Player playerNick went to bed!");
        verify(mockPlayer, times(1)).getDisplayName();
    }

    @Test
    @DisplayName("Test onPlayerAsleep() method with sleepEvent disabled")
    void onPlayerAsleepNoEvent() {
        TelegramBridge.config = mockConfig;
        TelegramBridge.config.sendToTelegram = true;
        TelegramBridge.config.sleepEvent = false;

        PlayerBedEnterEvent.BedEnterResult bedEnterResult = PlayerBedEnterEvent.BedEnterResult.OK;
        PlayerBedEnterEvent sleepEvent = new PlayerBedEnterEvent(mockPlayer, mock(Block.class), bedEnterResult);
        gameEvent.onPlayerAsleep(sleepEvent);

        verify(mockTelegramBot, never()).broadcastMessage(anyString());
        verify(mockPlayer, never()).getDisplayName();
    }

    @Test
    @DisplayName("Test onPlayerAsleep() method with sendToTelegram disabled")
    void onPlayerAsleepNoTelegram() {
        TelegramBridge.config = mockConfig;
        TelegramBridge.config.sendToTelegram = false;
        TelegramBridge.config.sleepEvent = true;

        PlayerBedEnterEvent.BedEnterResult bedEnterResult = PlayerBedEnterEvent.BedEnterResult.OK;
        PlayerBedEnterEvent sleepEvent = new PlayerBedEnterEvent(mockPlayer, mock(Block.class), bedEnterResult);
        gameEvent.onPlayerAsleep(sleepEvent);

        verify(mockTelegramBot, never()).broadcastMessage(anyString());
        verify(mockPlayer, never()).getDisplayName();
    }

    @Test
    @DisplayName("Test onAdvancement() method")
    void onAdvancement() {
        File translationsFolder = new File("src/test/resources");
        when(mockPlugin.getDataFolder()).thenReturn(translationsFolder);

        TelegramBridge.config = mockConfig;
        TelegramBridge.config.sendToTelegram = true;
        TelegramBridge.config.advancementEvent = true;
        TelegramBridge.plugin = mockPlugin;
        TelegramBridge.translations = new Translations(null);
        TelegramBridge.telegramBot = mockTelegramBot;

        when(mockPlayer.getDisplayName()).thenReturn("playerNick");

        Advancement mockAdvancement = mock(Advancement.class);
        when(mockAdvancement.getKey()).thenReturn(NamespacedKey.minecraft("story/root"));
        PlayerAdvancementDoneEvent advancementEvent = new PlayerAdvancementDoneEvent(mockPlayer, mockAdvancement);
        gameEvent.onAdvancement(advancementEvent);

        verify(mockTelegramBot, times(1)).broadcastMessage("Player playerNick made an advancement! Minecraft: The heart and story of the game");
        verify(mockPlayer, times(1)).getDisplayName();
    }

    @Test
    @DisplayName("Test onAdvancement() method with advancementEvent disabled")
    void onAdvancementNoEvent() {
        TelegramBridge.config = mockConfig;
        TelegramBridge.config.sendToTelegram = true;
        TelegramBridge.config.advancementEvent = false;

        Advancement mockAdvancement = mock(Advancement.class);
        when(mockAdvancement.getKey()).thenReturn(NamespacedKey.minecraft("story/root"));
        PlayerAdvancementDoneEvent advancementEvent = new PlayerAdvancementDoneEvent(mockPlayer, mockAdvancement);
        gameEvent.onAdvancement(advancementEvent);

        verify(mockTelegramBot, never()).broadcastMessage(anyString());
        verify(mockPlayer, never()).getDisplayName();
    }

    @Test
    @DisplayName("Test onAdvancement() method with sendToTelegram disabled")
    void onAdvancementNoTelegram() {
        TelegramBridge.config = mockConfig;
        TelegramBridge.config.sendToTelegram = false;
        TelegramBridge.config.advancementEvent = true;

        Advancement mockAdvancement = mock(Advancement.class);
        when(mockAdvancement.getKey()).thenReturn(NamespacedKey.minecraft("story/root"));
        PlayerAdvancementDoneEvent advancementEvent = new PlayerAdvancementDoneEvent(mockPlayer, mockAdvancement);
        gameEvent.onAdvancement(advancementEvent);

        verify(mockTelegramBot, never()).broadcastMessage(anyString());
        verify(mockPlayer, never()).getDisplayName();
    }

    @Test
    @DisplayName("Test onAdvancement() method with a recipe advancement")
    void onAdvancementRecipes() {
        TelegramBridge.config = mockConfig;
        TelegramBridge.config.sendToTelegram = true;
        TelegramBridge.config.advancementEvent = true;

        Advancement mockAdvancement = mock(Advancement.class);
        when(mockAdvancement.getKey()).thenReturn(NamespacedKey.minecraft("recipes/root"));
        PlayerAdvancementDoneEvent advancementEvent = new PlayerAdvancementDoneEvent(mockPlayer, mockAdvancement);
        gameEvent.onAdvancement(advancementEvent);

        verify(mockTelegramBot, never()).broadcastMessage(anyString());
        verify(mockPlayer, never()).getDisplayName();
    }

    @Test
    @DisplayName("Test onAdvancement() method with a dummy advancement key")
    void onAdvancementDummyKey() {
        File translationsFolder = new File("src/test/resources");
        when(mockPlugin.getDataFolder()).thenReturn(translationsFolder);

        TelegramBridge.config = mockConfig;
        TelegramBridge.config.sendToTelegram = true;
        TelegramBridge.config.advancementEvent = true;
        TelegramBridge.plugin = mockPlugin;
        TelegramBridge.translations = new Translations(null);
        TelegramBridge.telegramBot = mockTelegramBot;

        when(mockPlayer.getDisplayName()).thenReturn("playerNick");

        Advancement mockAdvancement = mock(Advancement.class);
        when(mockAdvancement.getKey()).thenReturn(NamespacedKey.minecraft("dummy/root"));
        PlayerAdvancementDoneEvent advancementEvent = new PlayerAdvancementDoneEvent(mockPlayer, mockAdvancement);
        gameEvent.onAdvancement(advancementEvent);

        verify(mockTelegramBot, times(1)).broadcastMessage("Player playerNick made an advancement! advancements.dummy.root.title: advancements.dummy.root.description");
        verify(mockPlayer, times(1)).getDisplayName();
    }
}
