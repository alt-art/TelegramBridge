package org.altart.telegrambridge.commands;

import org.altart.telegrambridge.Permissions;
import org.altart.telegrambridge.StandardMockTest;
import org.altart.telegrambridge.TelegramBridge;
import org.altart.telegrambridge.config.Config;
import org.altart.telegrambridge.config.Translations;
import org.altart.telegrambridge.database.SQLite;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ConfigCommandTest extends StandardMockTest {
    @Mock
    Command _command;

    @Mock
    Translations mockTranslations;
    @Mock
    Config mockConfig;
    @Mock
    SQLite mockDatabase;

    @Test
    @DisplayName("Test set default translations command")
    void onDefaultTranslationsCommand() throws Exception {
        CommandSender mockSender = mock(CommandSender.class);

        ConfigCommand configCommand = new ConfigCommand();

        TelegramBridge.translations = mockTranslations;
        TelegramBridge.config = mockConfig;

        when(mockSender.hasPermission(Permissions.DEFAULT_TRANSLATION_CONFIG.getString())).thenReturn(true);

        boolean result = configCommand.onCommand(mockSender, _command, "config", new String[]{"default-lang", "en"});

        verify(mockTranslations, times(1)).setDefaultLang("en");
        verify(mockConfig, times(1)).setLang("en");
        verify(mockSender, never()).sendMessage(anyString());
        verify(mockSender, times(1)).hasPermission(Permissions.DEFAULT_TRANSLATION_CONFIG.getString());
        assertTrue(result);
    }

    @Test
    @DisplayName("Test set default translations command without permission")
    void onDefaultTranslationsCommandWithoutPermission() throws Exception {
        CommandSender mockSender = mock(CommandSender.class);

        ConfigCommand configCommand = new ConfigCommand();

        TelegramBridge.translations = mockTranslations;
        TelegramBridge.config = mockConfig;

        when(mockSender.hasPermission(Permissions.DEFAULT_TRANSLATION_CONFIG.getString())).thenReturn(false);

        boolean result = configCommand.onCommand(mockSender, _command, "config", new String[]{"default-lang", "en"});

        verify(mockTranslations, never()).setDefaultLang("en");
        verify(mockConfig, never()).setLang("en");
        verify(mockSender, times(1)).sendMessage("You do not have permission to use this command.");
        verify(mockSender, times(1)).hasPermission(Permissions.DEFAULT_TRANSLATION_CONFIG.getString());
        assertFalse(result);
    }

    @Test
    @DisplayName("Test set default translations command with exception")
    void onDefaultTranslationsCommandWithException() throws Exception {
        CommandSender mockSender = mock(CommandSender.class);

        ConfigCommand configCommand = new ConfigCommand();

        TelegramBridge.translations = mockTranslations;
        TelegramBridge.config = mockConfig;

        when(mockSender.hasPermission(Permissions.DEFAULT_TRANSLATION_CONFIG.getString())).thenReturn(true);
        doThrow(new Exception("Test exception")).when(mockTranslations).setDefaultLang("en");

        boolean result = configCommand.onCommand(mockSender, _command, "config", new String[]{"default-lang", "en"});

        verify(mockTranslations, times(1)).setDefaultLang("en");
        verify(mockConfig, never()).setLang("en");
        verify(mockSender, times(1)).sendMessage("Test exception");
        verify(mockSender, times(1)).hasPermission(Permissions.DEFAULT_TRANSLATION_CONFIG.getString());
        assertFalse(result);
    }

    @Test
    @DisplayName("Test set translations command")
    void onTranslationsCommand() {
        Player mockSender = mock(Player.class);

        ConfigCommand configCommand = new ConfigCommand();

        TelegramBridge.database = mockDatabase;

        when(mockSender.hasPermission(Permissions.TRANSLATION_CONFIG.getString())).thenReturn(true);
        UUID uuid = UUID.randomUUID();

        when(mockSender.getUniqueId()).thenReturn(uuid);

        boolean result = configCommand.onCommand(mockSender, _command, "config", new String[]{"lang", "en"});

        verify(mockDatabase, times(1)).setLang(uuid, "en");
        verify(mockSender, never()).sendMessage(anyString());
        verify(mockSender, times(1)).hasPermission(Permissions.TRANSLATION_CONFIG.getString());
        assertTrue(result);
    }

    @Test
    @DisplayName("Test set translations command without permission")
    void onTranslationsCommandWithoutPermission() {
        Player mockSender = mock(Player.class);

        ConfigCommand configCommand = new ConfigCommand();

        TelegramBridge.database = mockDatabase;

        when(mockSender.hasPermission(Permissions.TRANSLATION_CONFIG.getString())).thenReturn(false);

        boolean result = configCommand.onCommand(mockSender, _command, "config", new String[]{"lang", "en"});

        verify(mockDatabase, never()).setLang(any(UUID.class), anyString());
        verify(mockSender, times(1)).sendMessage("You do not have permission to use this command.");
        verify(mockSender, times(1)).hasPermission(Permissions.TRANSLATION_CONFIG.getString());
        assertFalse(result);
    }

    @Test
    @DisplayName("Test set translations command without player")
    void onTranslationsCommandWithoutPlayer() {
        CommandSender mockSender = mock(CommandSender.class);

        ConfigCommand configCommand = new ConfigCommand();

        TelegramBridge.database = mockDatabase;

        when(mockSender.hasPermission(Permissions.TRANSLATION_CONFIG.getString())).thenReturn(true);

        boolean result = configCommand.onCommand(mockSender, _command, "config", new String[]{"lang", "en"});

        verify(mockDatabase, never()).setLang(any(UUID.class), anyString());
        verify(mockSender, times(1)).sendMessage("This command can only be used by players.");
        verify(mockSender, times(1)).hasPermission(Permissions.TRANSLATION_CONFIG.getString());
        assertFalse(result);
    }
}
