package org.altart.telegrambridge.commands;

import org.altart.telegrambridge.Permissions;
import org.altart.telegrambridge.StandardMockTest;
import org.altart.telegrambridge.TelegramBridge;
import org.altart.telegrambridge.config.Translations;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ConfigTabCompletionTest extends StandardMockTest {
    @Mock
    Command _command;
    @Mock
    CommandSender mockSender;
    @Mock
    Translations mockTranslations;

    @Test
    @DisplayName("Test tab completion first argument")
    void onTabComplete() {
        ConfigTabCompletion configTabCompletion = new ConfigTabCompletion();

        when(mockSender.hasPermission(Permissions.TRANSLATION_CONFIG.getString())).thenReturn(true);
        when(mockSender.hasPermission(Permissions.DEFAULT_TRANSLATION_CONFIG.getString())).thenReturn(true);

        String[] args = new String[1];

        List<String> result = configTabCompletion.onTabComplete(mockSender, _command, "config", args);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains("default-lang"));
        assertTrue(result.contains("lang"));
    }

    @Test
    @DisplayName("Test tab completion second argument default-lang")
    void onTabCompleteDefaultLang() {
        List<String> languages = new ArrayList<>();
        languages.add("en");
        languages.add("de");
        languages.add("fr");

        when(mockTranslations.getLoadedLanguages()).thenReturn(languages);

        TelegramBridge.translations = mockTranslations;

        ConfigTabCompletion configTabCompletion = new ConfigTabCompletion();

        when(mockSender.hasPermission(Permissions.DEFAULT_TRANSLATION_CONFIG.getString())).thenReturn(true);
        when(mockSender.hasPermission(Permissions.TRANSLATION_CONFIG.getString())).thenReturn(true);

        String[] args = new String[2];
        args[0] = "default-lang";

        List<String> result = configTabCompletion.onTabComplete(mockSender, _command, "config", args);

        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.contains("en"));
        assertTrue(result.contains("de"));
        assertTrue(result.contains("fr"));
    }

    @Test
    @DisplayName("Test tab completion second argument lang")
    void onTabCompleteLang() {
        List<String> languages = new ArrayList<>();
        languages.add("en");
        languages.add("de");
        languages.add("fr");

        when(mockTranslations.getLoadedLanguages()).thenReturn(languages);

        TelegramBridge.translations = mockTranslations;

        ConfigTabCompletion configTabCompletion = new ConfigTabCompletion();

        when(mockSender.hasPermission(Permissions.DEFAULT_TRANSLATION_CONFIG.getString())).thenReturn(true);
        when(mockSender.hasPermission(Permissions.TRANSLATION_CONFIG.getString())).thenReturn(true);

        String[] args = new String[2];
        args[0] = "lang";

        List<String> result = configTabCompletion.onTabComplete(mockSender, _command, "config", args);

        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.contains("en"));
        assertTrue(result.contains("de"));
        assertTrue(result.contains("fr"));
    }

    @Test
    @DisplayName("Test tab completion second argument lang without permission")
    void onTabCompleteLangWithoutPermission() {
        ConfigTabCompletion configTabCompletion = new ConfigTabCompletion();

        when(mockSender.hasPermission(Permissions.DEFAULT_TRANSLATION_CONFIG.getString())).thenReturn(true);
        when(mockSender.hasPermission(Permissions.TRANSLATION_CONFIG.getString())).thenReturn(false);

        String[] args = new String[2];
        args[0] = "lang";

        List<String> result = configTabCompletion.onTabComplete(mockSender, _command, "config", args);

        assertNull(result);
    }

    @Test
    @DisplayName("Test tab completion second argument default-lang without permission")
    void onTabCompleteDefaultLangWithoutPermission() {
        ConfigTabCompletion configTabCompletion = new ConfigTabCompletion();

        when(mockSender.hasPermission(Permissions.DEFAULT_TRANSLATION_CONFIG.getString())).thenReturn(false);
        when(mockSender.hasPermission(Permissions.TRANSLATION_CONFIG.getString())).thenReturn(true);

        String[] args = new String[2];
        args[0] = "default-lang";

        List<String> result = configTabCompletion.onTabComplete(mockSender, _command, "config", args);

        assertNull(result);
    }

    @Test
    @DisplayName("Test tab completion first argument without permission")
    void onTabCompleteWithoutPermission() {
        ConfigTabCompletion configTabCompletion = new ConfigTabCompletion();

        when(mockSender.hasPermission(Permissions.DEFAULT_TRANSLATION_CONFIG.getString())).thenReturn(false);
        when(mockSender.hasPermission(Permissions.TRANSLATION_CONFIG.getString())).thenReturn(false);

        String[] args = new String[1];

        List<String> result = configTabCompletion.onTabComplete(mockSender, _command, "config", args);

        assert result != null;
        assertEquals(0, result.size());
    }
}
