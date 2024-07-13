package org.altart.telegrambridge.events;

import org.altart.telegrambridge.Permissions;
import org.altart.telegrambridge.StandardMockTest;
import org.altart.telegrambridge.TelegramBridge;
import org.altart.telegrambridge.bot.TelegramBot;
import org.altart.telegrambridge.config.Config;
import org.altart.telegrambridge.config.Translations;
import org.bukkit.entity.*;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.Mock;

class ChatEventTest extends StandardMockTest {
    @Mock
    Player mockPlayer;

    @Mock
    Config mockConfig;

    @Mock
    TelegramBot mockTelegramBot;

    @Mock
    Plugin mockPlugin;

    @BeforeEach
    void setUp() {
        when(mockPlayer.getDisplayName()).thenReturn("playerNick");
    }

    @Test
    @DisplayName("Chat event test")
    void onChat() {
        when(mockPlayer.hasPermission(anyString())).thenReturn(true);
        ChatEvent chatEvent = new ChatEvent();
        Set<Player> players = new HashSet<>();
        players.add(mockPlayer);

        AsyncPlayerChatEvent event = new AsyncPlayerChatEvent(true, mockPlayer, "message", players);
        assertEquals("message", event.getMessage());

        File translationsFolder = new File("src/test/resources");
        when(mockPlugin.getDataFolder()).thenReturn(translationsFolder);

        doNothing().when(mockTelegramBot).broadcastMessage(anyString());
        doNothing().when(mockConfig).load();

        TelegramBridge.plugin = mockPlugin;
        TelegramBridge.telegramBot = mockTelegramBot;
        TelegramBridge.config = mockConfig;
        TelegramBridge.config.sendToTelegram = true;
        TelegramBridge.translations = new Translations(null);

        chatEvent.onChat(event);

        verify(mockTelegramBot, times(1)).broadcastMessage("[playerNick]: message");
        verify(mockPlayer, times(1)).hasPermission(Permissions.SEND.getString());
        verify(mockPlayer, times(1)).getDisplayName();
    }

    @Test
    @DisplayName("Chat event test with no permissions")
    void onChatNoPermission() {
        when(mockPlayer.hasPermission(anyString())).thenReturn(false);
        ChatEvent chatEvent = new ChatEvent();
        Set<Player> players = new HashSet<>();
        players.add(mockPlayer);

        AsyncPlayerChatEvent event = new AsyncPlayerChatEvent(true, mockPlayer, "message", players);
        assertEquals("message", event.getMessage());

        doNothing().when(mockConfig).load();

        TelegramBridge.config = mockConfig;
        TelegramBridge.config.sendToTelegram = true;

        chatEvent.onChat(event);

        verify(mockPlayer, times(1)).hasPermission(Permissions.SEND.getString());
        verify(mockPlayer, never()).getDisplayName();
        verify(mockTelegramBot, never()).broadcastMessage(anyString());
    }

    @Test
    @DisplayName("Chat event test with sendToTelegram false")
    void onChatDoNotSendToTelegram() {
        when(mockPlayer.hasPermission(anyString())).thenReturn(true);
        ChatEvent chatEvent = new ChatEvent();
        Set<Player> players = new HashSet<>();
        players.add(mockPlayer);

        AsyncPlayerChatEvent event = new AsyncPlayerChatEvent(true, mockPlayer, "message", players);
        assertEquals("message", event.getMessage());

        doNothing().when(mockConfig).load();

        TelegramBridge.config = mockConfig;
        TelegramBridge.config.sendToTelegram = false;

        chatEvent.onChat(event);

        verify(mockPlayer, times(1)).hasPermission(Permissions.SEND.getString());
        verify(mockPlayer, never()).getDisplayName();
        verify(mockTelegramBot, never()).broadcastMessage(anyString());
    }

    @Test
    @DisplayName("Chat event test with empty message")
    void onChatEmptyMessage() {
        when(mockPlayer.hasPermission(anyString())).thenReturn(true);
        ChatEvent chatEvent = new ChatEvent();
        Set<Player> players = new HashSet<>();
        players.add(mockPlayer);

        AsyncPlayerChatEvent event = new AsyncPlayerChatEvent(true, mockPlayer, "", players);
        assertEquals("", event.getMessage());

        doNothing().when(mockConfig).load();

        TelegramBridge.config = mockConfig;
        TelegramBridge.config.sendToTelegram = true;

        chatEvent.onChat(event);

        verify(mockPlayer, times(1)).hasPermission(Permissions.SEND.getString());
        verify(mockPlayer, times(1)).getDisplayName();
        verify(mockTelegramBot, never()).broadcastMessage(anyString());
    }

    @Test
    @DisplayName("Chat event test with command")
    void onChatCommand() {
        when(mockPlayer.hasPermission(anyString())).thenReturn(true);
        ChatEvent chatEvent = new ChatEvent();
        Set<Player> players = new HashSet<>();
        players.add(mockPlayer);

        AsyncPlayerChatEvent event = new AsyncPlayerChatEvent(true, mockPlayer, "/command", players);
        assertEquals("/command", event.getMessage());

        doNothing().when(mockConfig).load();

        TelegramBridge.config = mockConfig;
        TelegramBridge.config.sendToTelegram = true;

        chatEvent.onChat(event);

        verify(mockPlayer, never()).hasPermission(anyString());
        verify(mockPlayer, never()).getDisplayName();
        verify(mockTelegramBot, never()).broadcastMessage(anyString());
    }
}
