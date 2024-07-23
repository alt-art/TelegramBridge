package org.altart.telegrambridge.bot;

import org.altart.telegrambridge.StandardMockTest;
import org.altart.telegrambridge.TelegramBridge;
import org.altart.telegrambridge.bot.feature.MessageListener;
import org.altart.telegrambridge.config.Config;
import org.altart.telegrambridge.config.Translations;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TelegramBotTest extends StandardMockTest {
    @Mock
    Plugin mockPlugin;
    @Mock
    Update mockUpdate;
    @Mock
    Message mockMessage;
    @Mock
    User mockUser;
    @Mock
    Server mockServer;
    @Mock
    Player mockPlayer;
    @Mock
    Player mockPlayer2;

    @Test
    @DisplayName("Message received")
    void onUpdateReceivedMessage() {
        File resourceFolder = new File("src/test/resources/");
        when(mockPlugin.getDataFolder()).thenReturn(resourceFolder);

        TelegramBridge.plugin = mockPlugin;
        TelegramBridge.log = mock(Logger.class);
        TelegramBridge.config = new Config();
        TelegramBridge.config.chats.get(0).id = "123456789";

        when(mockUpdate.getMessage()).thenReturn(mockMessage);

        when(mockMessage.getText()).thenReturn("/test command");
        when(mockMessage.getFrom()).thenReturn(mockUser);
        when(mockMessage.hasText()).thenReturn(true);
        when(mockMessage.getChatId()).thenReturn(123456789L);

        MessageListener mockMessageListener = mock(MessageListener.class);

        TelegramBot telegramBot = new TelegramBot(mockPlugin);
        telegramBot.features = new ArrayList<>(Collections.singletonList(mockMessageListener));
        telegramBot.onUpdateReceived(mockUpdate);

        verify(mockMessageListener, times(1)).onUpdateReceived(any());
        verify(mockMessage, times(2)).getText();
    }

    @Test
    @DisplayName("Message received null")
    void onUpdateReceivedMessageNull() {
        File resourceFolder = new File("src/test/resources/");
        when(mockPlugin.getDataFolder()).thenReturn(resourceFolder);

        TelegramBridge.plugin = mockPlugin;
        TelegramBridge.log = mock(Logger.class);
        TelegramBridge.config = new Config();
        TelegramBridge.config.chats.get(0).id = "123456789";

        when(mockUpdate.getMessage()).thenReturn(null);

        MessageListener mockMessageListener = mock(MessageListener.class);

        TelegramBot telegramBot = new TelegramBot(mockPlugin);
        telegramBot.features = new ArrayList<>(Collections.singletonList(mockMessageListener));
        telegramBot.onUpdateReceived(mockUpdate);

        verify(mockMessageListener, never()).onUpdateReceived(any());
        verify(mockMessage, never()).getText();
    }

    @Test
    @DisplayName("Message received from bot")
    void onUpdateReceivedMessageFromBot() {
        File resourceFolder = new File("src/test/resources/");
        when(mockPlugin.getDataFolder()).thenReturn(resourceFolder);

        TelegramBridge.plugin = mockPlugin;
        TelegramBridge.log = mock(Logger.class);
        TelegramBridge.config = new Config();
        TelegramBridge.config.chats.get(0).id = "123456789";

        when(mockUpdate.getMessage()).thenReturn(mockMessage);

        when(mockMessage.getFrom()).thenReturn(mockUser);
        when(mockUser.getIsBot()).thenReturn(true);

        MessageListener mockMessageListener = mock(MessageListener.class);

        TelegramBot telegramBot = new TelegramBot(mockPlugin);
        telegramBot.features = new ArrayList<>(Collections.singletonList(mockMessageListener));
        telegramBot.onUpdateReceived(mockUpdate);

        verify(mockMessageListener, never()).onUpdateReceived(any());
        verify(mockMessage, never()).getText();
    }

    @Test
    @DisplayName("Message received chat id not in config")
    void onUpdateReceivedMessageChatIdNotInConfig() {
        File resourceFolder = new File("src/test/resources/");
        when(mockPlugin.getDataFolder()).thenReturn(resourceFolder);

        TelegramBridge.plugin = mockPlugin;
        TelegramBridge.log = mock(Logger.class);
        TelegramBridge.config = new Config();
        TelegramBridge.config.chats.get(0).id = "123456789";

        when(mockUpdate.getMessage()).thenReturn(mockMessage);

        when(mockMessage.getFrom()).thenReturn(mockUser);
        when(mockUser.getIsBot()).thenReturn(false);
        when(mockMessage.getChatId()).thenReturn(987654321L);

        MessageListener mockMessageListener = mock(MessageListener.class);

        TelegramBot telegramBot = new TelegramBot(mockPlugin);
        telegramBot.features = new ArrayList<>(Collections.singletonList(mockMessageListener));
        telegramBot.onUpdateReceived(mockUpdate);

        verify(mockMessageListener, never()).onUpdateReceived(any());
        verify(mockMessage, never()).getText();
    }

    @Test
    @DisplayName("Message received not a command")
    void onUpdateReceivedMessageNotACommand() {
        File resourceFolder = new File("src/test/resources/");
        when(mockPlugin.getDataFolder()).thenReturn(resourceFolder);

        TelegramBridge.plugin = mockPlugin;
        TelegramBridge.log = mock(Logger.class);
        TelegramBridge.config = new Config();
        TelegramBridge.config.chats.get(0).id = "123456789";

        when(mockUpdate.getMessage()).thenReturn(mockMessage);

        when(mockMessage.getText()).thenReturn("test message");
        when(mockMessage.getFrom()).thenReturn(mockUser);
        when(mockUser.getIsBot()).thenReturn(false);
        when(mockMessage.hasText()).thenReturn(true);
        when(mockMessage.getChatId()).thenReturn(123456789L);

        MessageListener mockMessageListener = mock(MessageListener.class);

        TelegramBot telegramBot = new TelegramBot(mockPlugin);
        telegramBot.features = new ArrayList<>(Collections.singletonList(mockMessageListener));
        telegramBot.onUpdateReceived(mockUpdate);

        verify(mockMessageListener, times(1)).onUpdateReceived(any());
        verify(mockMessage, times(1)).getText();
    }

    @Test
    @DisplayName("Message received has text false")
    void onUpdateReceivedHasTextFalse() {
        File resourceFolder = new File("src/test/resources/");
        when(mockPlugin.getDataFolder()).thenReturn(resourceFolder);

        TelegramBridge.plugin = mockPlugin;
        TelegramBridge.log = mock(Logger.class);
        TelegramBridge.config = new Config();
        TelegramBridge.config.chats.get(0).id = "123456789";

        when(mockUpdate.getMessage()).thenReturn(mockMessage);

        when(mockMessage.getFrom()).thenReturn(mockUser);
        when(mockUser.getIsBot()).thenReturn(false);
        when(mockMessage.hasText()).thenReturn(false);
        when(mockMessage.getChatId()).thenReturn(123456789L);

        MessageListener mockMessageListener = mock(MessageListener.class);

        TelegramBot telegramBot = new TelegramBot(mockPlugin);
        telegramBot.features = new ArrayList<>(Collections.singletonList(mockMessageListener));
        telegramBot.onUpdateReceived(mockUpdate);

        verify(mockMessageListener, times(1)).onUpdateReceived(any());
        verify(mockMessage, never()).getText();
    }

    @Test
    @DisplayName("Message received no features")
    void getBotUsername() {
        File resourceFolder = new File("src/test/resources/");
        when(mockPlugin.getDataFolder()).thenReturn(resourceFolder);

        TelegramBridge.plugin = mockPlugin;
        TelegramBridge.config = new Config();

        TelegramBot telegramBot = new TelegramBot(mockPlugin);
        assertEquals("TelegramBridgeBot", telegramBot.getBotUsername());
    }

    @Test
    @DisplayName("Online command")
    void onlineCommand() {
        File resourceFolder = new File("src/test/resources/");
        when(mockPlugin.getDataFolder()).thenReturn(resourceFolder);
        when(mockPlugin.getServer()).thenReturn(mockServer);

        List<Player> players = new ArrayList<>();
        players.add(mockPlayer);
        players.add(mockPlayer2);

        when(mockServer.getOnlinePlayers()).thenAnswer(invocation -> players);
        when(mockPlayer.getDisplayName()).thenReturn("Player1");
        when(mockPlayer2.getDisplayName()).thenReturn("Player2");

        TelegramBridge.plugin = mockPlugin;
        TelegramBridge.log = mock(Logger.class);
        TelegramBridge.config = new Config();
        TelegramBridge.config.chats.get(0).id = "123456789";
        TelegramBridge.translations = new Translations("en");

        when(mockUpdate.getMessage()).thenReturn(mockMessage);

        when(mockMessage.getText()).thenReturn("/online");
        when(mockMessage.getFrom()).thenReturn(mockUser);
        when(mockMessage.hasText()).thenReturn(true);
        when(mockMessage.getChatId()).thenReturn(123456789L);
        when(mockMessage.getMessageId()).thenReturn(314159265);

        MessageListener mockMessageListener = mock(MessageListener.class);

        TelegramBot telegramBot = new TelegramBot(mockPlugin);
        TelegramBot spyTelegramBot = spy(telegramBot);
        doReturn(false).when(spyTelegramBot).isNotAdmin(any(),any());
        spyTelegramBot.features = new ArrayList<>(Collections.singletonList(mockMessageListener));
        spyTelegramBot.onUpdateReceived(mockUpdate);

        verify(mockMessageListener, times(1)).onUpdateReceived(any());
        verify(mockMessage, times(2)).getText();
        verify(spyTelegramBot, times(1)).reply("There are 2 players online\nPlayer1\nPlayer2", "123456789", 314159265);
        verify(spyTelegramBot, never()).isNotAdmin(any(), any());
    }

    @Test
    @DisplayName("Online command no players")
    void onlineCommandNoPlayers() {
        File resourceFolder = new File("src/test/resources/");
        when(mockPlugin.getDataFolder()).thenReturn(resourceFolder);
        when(mockPlugin.getServer()).thenReturn(mockServer);

        List<Player> players = new ArrayList<>();

        when(mockServer.getOnlinePlayers()).thenAnswer(invocation -> players);

        TelegramBridge.plugin = mockPlugin;
        TelegramBridge.log = mock(Logger.class);
        TelegramBridge.config = new Config();
        TelegramBridge.config.chats.get(0).id = "123456789";
        TelegramBridge.translations = new Translations("en");

        when(mockUpdate.getMessage()).thenReturn(mockMessage);

        when(mockMessage.getText()).thenReturn("/online");
        when(mockMessage.getFrom()).thenReturn(mockUser);
        when(mockMessage.hasText()).thenReturn(true);
        when(mockMessage.getChatId()).thenReturn(123456789L);
        when(mockMessage.getMessageId()).thenReturn(314159265);

        MessageListener mockMessageListener = mock(MessageListener.class);

        TelegramBot telegramBot = new TelegramBot(mockPlugin);
        TelegramBot spyTelegramBot = spy(telegramBot);
        doReturn(false).when(spyTelegramBot).isNotAdmin(any(),any());
        spyTelegramBot.features = new ArrayList<>(Collections.singletonList(mockMessageListener));
        spyTelegramBot.onUpdateReceived(mockUpdate);

        verify(mockMessageListener, times(1)).onUpdateReceived(any());
        verify(mockMessage, times(2)).getText();
        verify(spyTelegramBot, times(1)).reply("There are 0 players online", "123456789", 314159265);
        verify(spyTelegramBot, never()).isNotAdmin(any(), any());
    }

    @Test
    @DisplayName("Set pin command")
    void setPinCommand() {
        File resourceFolder = new File("src/test/resources/");
        when(mockPlugin.getDataFolder()).thenReturn(resourceFolder);
        when(mockPlugin.getServer()).thenReturn(mockServer);

        List<Player> players = new ArrayList<>();
        players.add(mockPlayer);
        players.add(mockPlayer2);

        when(mockServer.getOnlinePlayers()).thenAnswer(invocation -> players);
        when(mockPlayer.getDisplayName()).thenReturn("Player1");
        when(mockPlayer2.getDisplayName()).thenReturn("Player2");

        TelegramBridge.plugin = mockPlugin;
        TelegramBridge.log = mock(Logger.class);
        TelegramBridge.config = new Config();
        TelegramBridge.config.chats.get(0).id = "123456789";
        TelegramBridge.translations = new Translations("en");

        when(mockUpdate.getMessage()).thenReturn(mockMessage);

        when(mockMessage.getText()).thenReturn("/setpin");
        when(mockMessage.getFrom()).thenReturn(mockUser);
        when(mockMessage.hasText()).thenReturn(true);
        when(mockMessage.getChatId()).thenReturn(123456789L);
        when(mockMessage.getMessageId()).thenReturn(314159265);

        MessageListener mockMessageListener = mock(MessageListener.class);

        TelegramBot telegramBot = new TelegramBot(mockPlugin);
        TelegramBot mockTelegramBot = spy(telegramBot);
        doReturn(false).when(mockTelegramBot).isNotAdmin(any(),any());
        doReturn(mockMessage).when(mockTelegramBot).sendSystemMessage(any(), any(), any());
        mockTelegramBot.features = new ArrayList<>(Collections.singletonList(mockMessageListener));
        mockTelegramBot.onUpdateReceived(mockUpdate);

        verify(mockTelegramBot, times(1)).sendSystemMessage("Hey welcome to the chat!\nThere are 0 players online", "123456789", 0);
        verify(mockTelegramBot, times(1)).pinMessage("123456789", 314159265);
        verify(mockTelegramBot, times(1)).isNotAdmin(any(), any());
    }

    @Test
    @DisplayName("Set pin command no players")
    void unsetPinCommand() {
        File resourceFolder = new File("src/test/resources/");
        when(mockPlugin.getDataFolder()).thenReturn(resourceFolder);

        TelegramBridge.plugin = mockPlugin;
        TelegramBridge.log = mock(Logger.class);
        TelegramBridge.config = new Config();
        TelegramBridge.config.chats.get(0).id = "123456789";
        TelegramBridge.config.chats.get(0).pinnedMessageId = 314159265;
        TelegramBridge.translations = new Translations("en");

        when(mockUpdate.getMessage()).thenReturn(mockMessage);

        when(mockMessage.getText()).thenReturn("/unsetpin");
        when(mockMessage.getFrom()).thenReturn(mockUser);
        when(mockMessage.hasText()).thenReturn(true);
        when(mockMessage.getChatId()).thenReturn(123456789L);
        when(mockMessage.getMessageId()).thenReturn(314159265);

        TelegramBot telegramBot = new TelegramBot(mockPlugin);
        TelegramBot mockTelegramBot = spy(telegramBot);
        doReturn(false).when(mockTelegramBot).isNotAdmin(any(),any());
        mockTelegramBot.onUpdateReceived(mockUpdate);

        verify(mockTelegramBot, times(1)).unpinMessage("123456789", 314159265);
        verify(mockTelegramBot, times(1)).deleteMessage("123456789", 314159265);
        assertNull(TelegramBridge.config.chats.get(0).pinnedMessageId);
        verify(mockTelegramBot, times(1)).isNotAdmin(any(), any());
    }

    @Test
    @DisplayName("Time command")
    void timeCommand() {
        File resourceFolder = new File("src/test/resources/");
        when(mockPlugin.getDataFolder()).thenReturn(resourceFolder);
        when(mockPlugin.getServer()).thenReturn(mockServer);

        TelegramBridge.plugin = mockPlugin;
        TelegramBridge.log = mock(Logger.class);
        TelegramBridge.config = new Config();
        TelegramBridge.config.chats.get(0).id = "123456789";
        TelegramBridge.translations = new Translations("en");

        when(mockUpdate.getMessage()).thenReturn(mockMessage);

        when(mockMessage.getText()).thenReturn("/time");
        when(mockMessage.getFrom()).thenReturn(mockUser);
        when(mockMessage.hasText()).thenReturn(true);
        when(mockMessage.getChatId()).thenReturn(123456789L);
        when(mockMessage.getMessageId()).thenReturn(314159265);

        List<World> worlds = new ArrayList<>();
        World world = mock(World.class);
        worlds.add(world);
        when(mockServer.getWorlds()).thenReturn(worlds);
        when(world.getTime()).thenReturn(24000L);
        when(world.getFullTime()).thenReturn(24000L*1000);

        MessageListener mockMessageListener = mock(MessageListener.class);

        TelegramBot telegramBot = new TelegramBot(mockPlugin);
        TelegramBot spyTelegramBot = spy(telegramBot);
        doReturn(false).when(spyTelegramBot).isNotAdmin(any(),any());
        spyTelegramBot.features = new ArrayList<>(Collections.singletonList(mockMessageListener));
        spyTelegramBot.onUpdateReceived(mockUpdate);

        verify(mockMessageListener, times(1)).onUpdateReceived(any());
        verify(mockMessage, times(2)).getText();
        verify(spyTelegramBot, times(1)).reply(
                "Time is 24:00 \n"+
                "Date is September 26, Year 2",
                "123456789",
                314159265
        );
        verify(spyTelegramBot, never()).isNotAdmin(any(), any());
    }

    @Test
    @DisplayName("Set thread command")
    void setThreadCommand() {
        File resourceFolder = new File("src/test/resources/");
        when(mockPlugin.getDataFolder()).thenReturn(resourceFolder);

        TelegramBridge.plugin = mockPlugin;
        TelegramBridge.log = mock(Logger.class);
        TelegramBridge.config = new Config();
        TelegramBridge.config.chats.get(0).id = "123456789";
        TelegramBridge.translations = new Translations("en");

        when(mockUpdate.getMessage()).thenReturn(mockMessage);

        when(mockMessage.getText()).thenReturn("/setthread");
        when(mockMessage.getFrom()).thenReturn(mockUser);
        when(mockMessage.hasText()).thenReturn(true);
        when(mockMessage.getChatId()).thenReturn(123456789L);
        when(mockMessage.getMessageId()).thenReturn(314159265);

        MessageListener mockMessageListener = mock(MessageListener.class);

        TelegramBot telegramBot = new TelegramBot(mockPlugin);
        TelegramBot mockTelegramBot = spy(telegramBot);
        doReturn(false).when(mockTelegramBot).isNotAdmin(any(),any());
        doReturn(mockMessage).when(mockTelegramBot).sendSystemMessage(any(), any(), any());
        mockTelegramBot.features = new ArrayList<>(Collections.singletonList(mockMessageListener));
        mockTelegramBot.onUpdateReceived(mockUpdate);

        verify(mockTelegramBot, times(1)).reply("Thread set", "123456789", 314159265);
        verify(mockTelegramBot, times(1)).isNotAdmin(any(), any());
    }

    @Test
    @DisplayName("Unset thread command")
    void unsetThreadCommand() {
        File resourceFolder = new File("src/test/resources/");
        when(mockPlugin.getDataFolder()).thenReturn(resourceFolder);

        TelegramBridge.plugin = mockPlugin;
        TelegramBridge.log = mock(Logger.class);
        TelegramBridge.config = new Config();
        TelegramBridge.config.chats.get(0).id = "123456789";
        TelegramBridge.config.chats.get(0).thread = 123456789;
        TelegramBridge.translations = new Translations("en");


        when(mockUpdate.getMessage()).thenReturn(mockMessage);

        when(mockMessage.getText()).thenReturn("/unsetthread");
        when(mockMessage.getFrom()).thenReturn(mockUser);
        when(mockMessage.hasText()).thenReturn(true);
        when(mockMessage.getChatId()).thenReturn(123456789L);
        when(mockMessage.getMessageId()).thenReturn(314159265);

        MessageListener mockMessageListener = mock(MessageListener.class);

        TelegramBot telegramBot = new TelegramBot(mockPlugin);
        TelegramBot mockTelegramBot = spy(telegramBot);
        doReturn(false).when(mockTelegramBot).isNotAdmin(any(),any());
        mockTelegramBot.features = new ArrayList<>(Collections.singletonList(mockMessageListener));
        mockTelegramBot.onUpdateReceived(mockUpdate);

        verify(mockTelegramBot, times(1)).reply("Thread unset", "123456789", 314159265);
        verify(mockTelegramBot, times(1)).isNotAdmin(any(), any());
        assertNull(TelegramBridge.config.chats.get(0).thread);
    }
}
