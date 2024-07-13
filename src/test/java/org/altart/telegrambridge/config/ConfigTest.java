package org.altart.telegrambridge.config;

import org.altart.telegrambridge.StandardMockTest;
import org.altart.telegrambridge.TelegramBridge;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ConfigTest extends StandardMockTest {

    @Mock
    Plugin mockPlugin;

    @Test
    @DisplayName("Load config")
    void load() {
        File resourceFolder = new File("src/test/resources/");
        when(mockPlugin.getDataFolder()).thenReturn(resourceFolder);

        TelegramBridge.plugin = mockPlugin;
        TelegramBridge.log = mock(Logger.class);

        Config config = new Config();
        verify(mockPlugin, times(1)).getDataFolder();
        assertEquals("BT", config.botToken);
        assertEquals("123456789", config.chats.get(0).id);
    }

    @Test
    @DisplayName("Create config")
    void create() {
        File resourceFolder = new File("src/test/resources/empty");
        when(mockPlugin.getDataFolder()).thenReturn(resourceFolder);

        TelegramBridge.plugin = mockPlugin;
        Logger log = mock(Logger.class);
        TelegramBridge.log = log;

        Config config = new Config();
        verify(mockPlugin, times(1)).getDataFolder();
        assertEquals("YOUR_BOT_TOKEN", config.botToken);
        assertEquals("YOUR_CHAT_ID", config.chats.get(0).id);
        verify(log, times(1)).warning(anyString());
        verify(log, times(1)).info(anyString());

        assertTrue(new File("src/test/resources/empty/config.yml").delete());
    }

    @Test
    @DisplayName("Save config")
    void save() {
        File resourceFolder = new File("src/test/resources/empty");
        when(mockPlugin.getDataFolder()).thenReturn(resourceFolder);

        TelegramBridge.plugin = mockPlugin;
        Logger log = mock(Logger.class);
        TelegramBridge.log = log;

        Config config = new Config();
        config.botToken = "BT";
        config.chats.get(0).id = "ID";
        config.chats.get(0).thread = 77;
        config.chats.get(0).pinnedMessageId = 77;
        config.sendToTelegram = !config.sendToTelegram;
        config.sendToChat = !config.sendToChat;
        config.joinAndLeaveEvent = !config.joinAndLeaveEvent;
        config.deathEvent = !config.deathEvent;
        config.sleepEvent = !config.sleepEvent;
        config.advancementEvent = !config.advancementEvent;
        config.lang = "LANG";
        config.pinned = "PINNED";
        config.save();

        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(new File("src/test/resources/empty/config.yml"));
        Field[] fields = config.getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                if (yaml.contains(field.getName())) {
                    if (field.getType().equals(String.class)) {
                        assertEquals(field.get(config), yaml.get(field.getName()));
                    } else if (field.getType().equals(Boolean.class)) {
                        assertEquals(field.get(config), yaml.getBoolean(field.getName()));
                    } else if (field.getType().equals(Integer.class)) {
                        assertEquals(field.get(config), yaml.getInt(field.getName()));
                    } else if (field.getType().equals(List.class)) {
                        Config.Chat chat = Config.Chat.chatsFrom(yaml.getMapList(field.getName())).get(0);
                        assertEquals(chat.id, config.chats.get(0).id);
                        assertEquals(chat.thread, config.chats.get(0).thread);
                        assertEquals(chat.pinnedMessageId, config.chats.get(0).pinnedMessageId);
                    }
                }
            } catch (Exception e) {
                fail();
            }
        }
        verify(log, times(1)).warning(anyString());
        verify(log, times(1)).info(anyString());
        assertTrue(new File("src/test/resources/empty/config.yml").delete());
    }

    @Test
    @DisplayName("Set PinnedMessageId")
    void setPinnedMessageId() {
        File resourceFolder = new File("src/test/resources/empty");
        when(mockPlugin.getDataFolder()).thenReturn(resourceFolder);

        TelegramBridge.plugin = mockPlugin;
        Logger log = mock(Logger.class);
        TelegramBridge.log = log;

        Config config = new Config();

        config.chats.get(0).id = "ID";
        config.setPinnedMessageId("ID", 77);
        assertEquals(77, config.chats.get(0).pinnedMessageId);

        config.setPinnedMessageId("BANANA", 66);
        assertNotEquals(66, config.chats.get(0).pinnedMessageId);

        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(new File("src/test/resources/empty/config.yml"));
        Config.Chat chat = Config.Chat.chatsFrom(yaml.getMapList("chats")).get(0);
        assertEquals(77, chat.pinnedMessageId);
        assertEquals("ID", chat.id);

        verify(log, times(1)).warning(anyString());
        verify(log, times(1)).info(anyString());
        assertTrue(new File("src/test/resources/empty/config.yml").delete());
    }

    @Test
    @DisplayName("Set Lang")
    void setLang() {
        File resourceFolder = new File("src/test/resources/empty");
        when(mockPlugin.getDataFolder()).thenReturn(resourceFolder);

        TelegramBridge.plugin = mockPlugin;
        Logger log = mock(Logger.class);
        TelegramBridge.log = log;

        Config config = new Config();

        config.setLang("LANG");
        assertEquals("LANG", config.lang);

        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(new File("src/test/resources/empty/config.yml"));
        assertEquals("LANG", yaml.get("lang"));

        verify(log, times(1)).warning(anyString());
        verify(log, times(1)).info(anyString());
        assertTrue(new File("src/test/resources/empty/config.yml").delete());
    }

    @Test
    @DisplayName("Set Thread")
    void setThread() {
        File resourceFolder = new File("src/test/resources/empty");
        when(mockPlugin.getDataFolder()).thenReturn(resourceFolder);

        TelegramBridge.plugin = mockPlugin;
        Logger log = mock(Logger.class);
        TelegramBridge.log = log;

        Config config = new Config();

        config.chats.get(0).id = "ID";
        config.setThread("ID", 77);
        assertEquals(77, config.chats.get(0).thread);

        config.setThread("BANANA", 66);
        assertNotEquals(66, config.chats.get(0).thread);

        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(new File("src/test/resources/empty/config.yml"));
        Config.Chat chat = Config.Chat.chatsFrom(yaml.getMapList("chats")).get(0);
        assertEquals(77, chat.thread);
        assertEquals("ID", chat.id);

        verify(log, times(1)).warning(anyString());
        verify(log, times(1)).info(anyString());
        assertTrue(new File("src/test/resources/empty/config.yml").delete());
    }
}
