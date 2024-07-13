package org.altart.telegrambridge.database;

import org.altart.telegrambridge.StandardMockTest;
import org.altart.telegrambridge.TelegramBridge;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.io.File;
import java.util.UUID;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SQLiteTest extends StandardMockTest {
    @Mock
    Plugin mockPlugin;

    @BeforeEach
    public void setUp() {
        File translationsFolder = new File("src/test/resources");
        when(mockPlugin.getDataFolder()).thenReturn(translationsFolder);
        TelegramBridge.plugin = mockPlugin;
    }

    @AfterEach
    void removeDatabase() {
        File database = new File("src/test/resources/database.db");
        boolean delete = database.delete();
        assertTrue(delete);
    }

    @Test
    @DisplayName("Test sqlite getLang() and setLang() methods")
    void getLang() {
        SQLite sqlite = new SQLite();
        UUID uuid = UUID.randomUUID();
        sqlite.setLang(uuid, "en");
        assertEquals("en", sqlite.getLang(uuid));
        sqlite.close();
    }

    @Test
    @DisplayName("Test sqlite setLang() method with invalid language")
    void invalidLang() {
        SQLite sqlite = new SQLite();
        UUID uuid = UUID.randomUUID();
        assertThrows(IllegalArgumentException.class, () -> sqlite.setLang(uuid, "eng"));
        sqlite.close();
    }

    @Test
    @DisplayName("Test sqlite methods with SQLite connection closed to trigger SQLiteException")
    void sqliteException() {
        TelegramBridge.log = mock(Logger.class);

        SQLite sqlite = new SQLite();
        sqlite.close();
        UUID uuid = UUID.randomUUID();
        assertNull(sqlite.getLang(uuid));
        verify(TelegramBridge.log, times(92)).severe(anyString());

        SQLite sqlite2 = new SQLite();
        sqlite2.close();
        sqlite2.setLang(uuid, "en");
        verify(TelegramBridge.log, times(184)).severe(anyString());
    }
}
