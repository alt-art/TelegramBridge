package org.altart.telegrambridge.bot.feature;

import org.altart.telegrambridge.StandardMockTest;
import org.altart.telegrambridge.TelegramBridge;
import org.altart.telegrambridge.bot.TelegramBot;
import org.altart.telegrambridge.config.Config;
import org.altart.telegrambridge.config.Translations;
import org.altart.telegrambridge.database.SQLite;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.logging.Logger;

import static org.mockito.Mockito.*;

class SentMediaTest extends StandardMockTest {
    @Mock
    Logger mockLogger;
    @Mock
    Plugin mockPlugin;
    @Mock
    Server mockServer;
    @Mock
    TelegramBot mockTelegramBot;
    @Mock
    Update mockUpdate;
    @Mock
    Message mockMessage;
    @Mock
    User mockUser;
    @Mock
    SQLite mockDatabase;
    @Mock
    Player mockPlayer;
    @Mock
    Player mockPlayerNoPerm;

    @BeforeEach
    void setUp() {
        File resourceFolder = new File("src/test/resources/");
        when(mockPlugin.getDataFolder()).thenReturn(resourceFolder);

        TelegramBridge.plugin = mockPlugin;
        TelegramBridge.config = new Config();
        TelegramBridge.database = mockDatabase;
        TelegramBridge.translations = new Translations("en");

        when(mockServer.getLogger()).thenReturn(mockLogger);

        setServerMock(mockServer);

        when(mockPlayer.hasPermission(anyString())).thenReturn(true);

        when(mockPlayerNoPerm.hasPermission(anyString())).thenReturn(false);

        ArrayList<Player> players = new ArrayList<>();
        players.add(mockPlayer);
        players.add(mockPlayerNoPerm);

        when(mockServer.getOnlinePlayers()).thenAnswer(invocation -> players);

        when(mockUpdate.getMessage()).thenReturn(mockMessage);
        when(mockMessage.getFrom()).thenReturn(mockUser);
    }

    private void setServerMock(Server mock) {
        try {
            Field server = Bukkit.class.getDeclaredField("server");
            server.setAccessible(true);
            server.set(server, mock);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Update received")
    void onUpdateReceived() {
        when(mockMessage.hasPhoto()).thenReturn(true);

        SentMedia sentMedia = new SentMedia(mockTelegramBot);
        sentMedia.onUpdateReceived(mockUpdate);

        verify(mockMessage, times(2)).hasPhoto();
        verify(mockDatabase, times(1)).getLang(any());
        verify(mockPlayer, times(1)).hasPermission("telegrambridge.receive");
        verify(mockPlayer, times(1)).sendMessage("§7[§bTelegram§7] §f[] sent an image");
        verify(mockPlayer, times(1)).getUniqueId();
        verify(mockPlayerNoPerm, times(1)).hasPermission("telegrambridge.receive");
        verify(mockPlayerNoPerm, never()).sendMessage(anyString());
        verify(mockPlayerNoPerm, never()).getUniqueId();

        when(mockMessage.getCaption()).thenReturn("caption");
        sentMedia.onUpdateReceived(mockUpdate);
        verify(mockMessage, times(4)).hasPhoto();
        verify(mockDatabase, times(2)).getLang(any());
        verify(mockPlayer, times(2)).hasPermission("telegrambridge.receive");
        verify(mockPlayer, times(1)).sendMessage("§7[§bTelegram§7] §f[] sent an image\ncaption");
        verify(mockPlayer, times(2)).getUniqueId();
        verify(mockPlayerNoPerm, times(2)).hasPermission("telegrambridge.receive");
        verify(mockPlayerNoPerm, never()).sendMessage(anyString());
        verify(mockPlayerNoPerm, never()).getUniqueId();
    }

    @Test
    @DisplayName("Update received with no photo")
    void onUpdateReceivedPhoto() {
        when(mockMessage.hasPhoto()).thenReturn(true);
        when(mockMessage.getCaption()).thenReturn("caption");

        SentMedia sentMedia = new SentMedia(mockTelegramBot);
        sentMedia.onUpdateReceived(mockUpdate);

        verify(mockPlayer, times(1)).sendMessage("§7[§bTelegram§7] §f[] sent an image\ncaption");
    }

    @Test
    @DisplayName("Update received with video")
    void onUpdateReceivedVideo() {
        when(mockMessage.hasVideo()).thenReturn(true);
        when(mockMessage.getCaption()).thenReturn("caption");

        SentMedia sentMedia = new SentMedia(mockTelegramBot);
        sentMedia.onUpdateReceived(mockUpdate);

        verify(mockPlayer, times(1)).sendMessage("§7[§bTelegram§7] §f[] sent a video\ncaption");
    }

    @Test
    @DisplayName("Update received with document")
    void onUpdateReceivedDocument() {
        when(mockMessage.hasDocument()).thenReturn(true);
        when(mockMessage.getCaption()).thenReturn("caption");

        SentMedia sentMedia = new SentMedia(mockTelegramBot);
        sentMedia.onUpdateReceived(mockUpdate);

        verify(mockPlayer, times(1)).sendMessage("§7[§bTelegram§7] §f[] sent a document\ncaption");
    }

    @Test
    @DisplayName("Update received with audio")
    void onUpdateReceivedAudio() {
        when(mockMessage.hasAudio()).thenReturn(true);
        when(mockMessage.getCaption()).thenReturn("caption");

        SentMedia sentMedia = new SentMedia(mockTelegramBot);
        sentMedia.onUpdateReceived(mockUpdate);

        verify(mockPlayer, times(1)).sendMessage("§7[§bTelegram§7] §f[] sent an audio\ncaption");
    }

    @Test
    @DisplayName("Update received with voice")
    void onUpdateReceivedVoice() {
        when(mockMessage.hasVoice()).thenReturn(true);
        when(mockMessage.getCaption()).thenReturn("caption");

        SentMedia sentMedia = new SentMedia(mockTelegramBot);
        sentMedia.onUpdateReceived(mockUpdate);

        verify(mockPlayer, times(1)).sendMessage("§7[§bTelegram§7] §f[] sent a voice\ncaption");
    }

    @Test
    @DisplayName("Update received with sticker")
    void onUpdateReceivedSticker() {
        when(mockMessage.hasSticker()).thenReturn(true);
        when(mockMessage.getCaption()).thenReturn("caption");

        SentMedia sentMedia = new SentMedia(mockTelegramBot);
        sentMedia.onUpdateReceived(mockUpdate);

        verify(mockPlayer, times(1)).sendMessage("§7[§bTelegram§7] §f[] sent a sticker\ncaption");
    }

    @Test
    @DisplayName("Update received with contact")
    void onUpdateReceivedContact() {
        when(mockMessage.hasContact()).thenReturn(true);
        when(mockMessage.getCaption()).thenReturn("caption");

        SentMedia sentMedia = new SentMedia(mockTelegramBot);
        sentMedia.onUpdateReceived(mockUpdate);

        verify(mockPlayer, times(1)).sendMessage("§7[§bTelegram§7] §f[] sent a contact\ncaption");
    }

    @Test
    @DisplayName("Update received with location")
    void onUpdateReceivedLocation() {
        when(mockMessage.hasLocation()).thenReturn(true);
        when(mockMessage.getCaption()).thenReturn("caption");

        SentMedia sentMedia = new SentMedia(mockTelegramBot);
        sentMedia.onUpdateReceived(mockUpdate);

        verify(mockPlayer, times(1)).sendMessage("§7[§bTelegram§7] §f[] sent a location\ncaption");
    }

    @Test
    @DisplayName("Update received with poll")
    void onUpdateReceivedPoll() {
        when(mockMessage.hasPoll()).thenReturn(true);
        when(mockMessage.getCaption()).thenReturn("caption");

        SentMedia sentMedia = new SentMedia(mockTelegramBot);
        sentMedia.onUpdateReceived(mockUpdate);

        verify(mockPlayer, times(1)).sendMessage("§7[§bTelegram§7] §f[] sent a poll\ncaption");
    }

    @Test
    @DisplayName("Update received is not media")
    void onUpdateReceivedIsNotMedia() {
        SentMedia sentMedia = new SentMedia(mockTelegramBot);
        sentMedia.onUpdateReceived(mockUpdate);

        verify(mockMessage, times(1)).hasPhoto();
    }

    @Test
    @DisplayName("Update received with send to chat false")
    void onUpdateReceivedSendToChatFalse() {
        File resourceFolder = new File("src/test/resources/");
        when(mockPlugin.getDataFolder()).thenReturn(resourceFolder);

        TelegramBridge.plugin = mockPlugin;
        TelegramBridge.config = new Config();
        TelegramBridge.translations = new Translations("en");
        TelegramBridge.config.sendToChat = false;

        when(mockUpdate.getMessage()).thenReturn(mockMessage);
        when(mockMessage.hasPhoto()).thenReturn(true);
        when(mockMessage.getFrom()).thenReturn(mockUser);

        SentMedia sentMedia = new SentMedia(mockTelegramBot);
        sentMedia.onUpdateReceived(mockUpdate);

        verify(mockServer, never()).getOnlinePlayers();
        verify(mockMessage, never()).hasPhoto();
        verify(mockDatabase, never()).getLang(any());
        verify(mockPlayer, never()).hasPermission(anyString());
        verify(mockPlayer, never()).sendMessage(anyString());
        verify(mockPlayer, never()).getUniqueId();
        verify(mockPlayerNoPerm, never()).hasPermission(anyString());
        verify(mockPlayerNoPerm, never()).sendMessage(anyString());
        verify(mockPlayerNoPerm, never()).getUniqueId();
    }
}
