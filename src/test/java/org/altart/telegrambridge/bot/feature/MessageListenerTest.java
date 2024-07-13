package org.altart.telegrambridge.bot.feature;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.altart.telegrambridge.StandardMockTest;
import org.altart.telegrambridge.TelegramBridge;
import org.altart.telegrambridge.bot.TelegramBot;
import org.altart.telegrambridge.config.Config;
import org.altart.telegrambridge.config.Translations;
import org.altart.telegrambridge.database.SQLite;
import org.altart.telegrambridge.utils.ComponentMatcher;
import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static org.mockito.Mockito.*;

class MessageListenerTest extends StandardMockTest {
    @Mock
    Plugin mockPlugin;
    @Mock
    TelegramBot mockTelegramBot;
    @Mock
    Update mockUpdate;
    @Mock
    Message mockMessage;
    @Mock
    Message mockReplyToMessage;
    @Mock
    Config mockConfig;
    @Mock
    User mockUser;
    @Mock
    Logger mockLogger;
    @Mock
    Server mockServer;
    @Mock
    SQLite mockSQLite;
    @Mock
    Player mockPlayer;
    @Mock
    Player.Spigot mockPlayerSpigot;
    @Mock
    Player mockPlayerNoReceivePerm;
    @Mock
    Player.Spigot mockPlayerNoReceivePermSpigot;
    @Mock
    Player mockPlayerNoReplyPerm;
    @Mock
    Player.Spigot mockPlayerNoReplyPermSpigot;
    final MockedStatic<RandomStringUtils> utils = mockStatic(RandomStringUtils.class);

    @BeforeEach
    void setUp() {
        File resourceFolder = new File("src/test/resources/");
        when(mockPlugin.getDataFolder()).thenReturn(resourceFolder);

        when(mockUpdate.getMessage()).thenReturn(mockMessage);
        when(mockMessage.getFrom()).thenReturn(mockUser);
        when(mockReplyToMessage.getFrom()).thenReturn(mockUser);

        TelegramBridge.config = mockConfig;
        TelegramBridge.log = mockLogger;
        TelegramBridge.database = mockSQLite;
        TelegramBridge.plugin = mockPlugin;
        TelegramBridge.translations = new Translations("en");

        utils.when(() -> RandomStringUtils.random(anyInt(), anyBoolean(), anyBoolean())).thenReturn("asdf", "asdf", "aaaa");

        List<Player> players = new ArrayList<>();

        when(mockPlayer.hasPermission(anyString())).thenReturn(true);
        when(mockPlayer.spigot()).thenReturn(mockPlayerSpigot);
        players.add(mockPlayer);

        when(mockPlayerNoReceivePerm.hasPermission(anyString())).thenReturn(false);
        when(mockPlayerNoReceivePerm.spigot()).thenReturn(mockPlayerNoReceivePermSpigot);
        players.add(mockPlayerNoReceivePerm);

        when(mockPlayerNoReplyPerm.hasPermission("telegrambridge.receive")).thenReturn(true);
        when(mockPlayerNoReplyPerm.spigot()).thenReturn(mockPlayerNoReplyPermSpigot);
        players.add(mockPlayerNoReplyPerm);

        when(mockServer.getOnlinePlayers()).thenAnswer(invocation -> players);
        when(mockServer.getLogger()).thenReturn(mockLogger);

        setServerMock(mockServer);
    }

    @AfterEach
    void tearDown() {
        utils.close();
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
        when(mockMessage.getText()).thenReturn("test");
        when(mockReplyToMessage.getText()).thenReturn("test reply");
        when(mockReplyToMessage.hasText()).thenReturn(true);
        when(mockMessage.getReplyToMessage()).thenReturn(mockReplyToMessage);
        when(mockUser.getUserName()).thenReturn("testUser");

        TelegramBridge.config.sendToChat = true;

        MessageListener messageListener = new MessageListener(mockTelegramBot);
        messageListener.onUpdateReceived(mockUpdate);

        BaseComponent finalComponent = new TextComponent("");
        TextComponent replyComponent = new TextComponent("- §breply to §7testUser: test reply...§r -\n");
        replyComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("test reply")));
        finalComponent.addExtra(replyComponent);
        finalComponent.addExtra(new TextComponent("§7[§bTelegram§7] §f[testUser] test"));


        verify(mockPlayerNoReplyPermSpigot, times(1)).sendMessage(argThat(new ComponentMatcher(finalComponent)));
        utils.verify(() -> RandomStringUtils.random(8, true, true), times(1));
        verify(mockPlayerNoReceivePermSpigot, never()).sendMessage(any(BaseComponent.class));

        TextComponent replyButtonComponent = new TextComponent(" [Reply]");
        replyButtonComponent.setColor(ChatColor.AQUA);
        replyButtonComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to reply")));
        replyButtonComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tbr asdf "));
        finalComponent.addExtra(replyButtonComponent);
        verify(mockPlayerSpigot, times(1)).sendMessage(argThat(new ComponentMatcher(finalComponent)));

        verify(mockMessage, times(2)).getText();

        messageListener.onUpdateReceived(mockUpdate);
        utils.verify(() -> RandomStringUtils.random(8, true, true), times(3));
    }

    @Test
    @DisplayName("Update received no text")
    void onUpdateReceivedNoText() {
        when(mockMessage.getText()).thenReturn(null);

        MessageListener messageListener = new MessageListener(mockTelegramBot);
        messageListener.onUpdateReceived(mockUpdate);

        verify(mockMessage, times(1)).getText();
        verify(mockPlayerSpigot, never()).sendMessage(any(BaseComponent.class));
        verify(mockPlayerNoReceivePermSpigot, never()).sendMessage(any(BaseComponent.class));
        verify(mockPlayerNoReplyPermSpigot, never()).sendMessage(any(BaseComponent.class));
    }

    @Test
    @DisplayName("Update received no send to chat")
    void onUpdateReceivedNoSendToChat() {
        when(mockMessage.getText()).thenReturn("test");

        TelegramBridge.config.sendToChat = false;

        MessageListener messageListener = new MessageListener(mockTelegramBot);
        messageListener.onUpdateReceived(mockUpdate);

        verify(mockMessage, times(1)).getText();
        verify(mockPlayerSpigot, never()).sendMessage(any(BaseComponent.class));
        verify(mockPlayerNoReceivePermSpigot, never()).sendMessage(any(BaseComponent.class));
        verify(mockPlayerNoReplyPermSpigot, never()).sendMessage(any(BaseComponent.class));
    }

    @Test
    @DisplayName("Update received command")
    void onUpdateReceivedCommand() {
        when(mockMessage.getText()).thenReturn("/command");

        MessageListener messageListener = new MessageListener(mockTelegramBot);
        messageListener.onUpdateReceived(mockUpdate);

        verify(mockMessage, times(1)).getText();
        verify(mockPlayerSpigot, never()).sendMessage(any(BaseComponent.class));
        verify(mockPlayerNoReceivePermSpigot, never()).sendMessage(any(BaseComponent.class));
        verify(mockPlayerNoReplyPermSpigot, never()).sendMessage(any(BaseComponent.class));
    }

    @Test
    @DisplayName("Update received no reply to message")
    void onUpdateReceivedNoReplyToMessage() {
        when(mockMessage.getText()).thenReturn("test");
        when(mockUser.getUserName()).thenReturn("testUser");

        TelegramBridge.config.sendToChat = true;

        MessageListener messageListener = new MessageListener(mockTelegramBot);
        messageListener.onUpdateReceived(mockUpdate);

        BaseComponent finalComponent = new TextComponent("");
        finalComponent.addExtra(new TextComponent("§7[§bTelegram§7] §f[testUser] test"));

        verify(mockPlayerNoReplyPermSpigot, times(1)).sendMessage(argThat(new ComponentMatcher(finalComponent)));

        TextComponent replyButtonComponent = new TextComponent(" [Reply]");
        replyButtonComponent.setColor(ChatColor.AQUA);
        replyButtonComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to reply")));
        replyButtonComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tbr asdf "));
        finalComponent.addExtra(replyButtonComponent);

        verify(mockPlayerSpigot, times(1)).sendMessage(argThat(new ComponentMatcher(finalComponent)));
        verify(mockPlayerNoReceivePermSpigot, never()).sendMessage(any(BaseComponent.class));

        verify(mockMessage, times(2)).getText();
    }

    @Test
    @DisplayName("Update received no reply to message no text")
    void onUpdateReceivedNoReplyToMessageNoText() {
        when(mockMessage.getText()).thenReturn("test");
        when(mockUser.getUserName()).thenReturn("testUser");
        when(mockReplyToMessage.hasText()).thenReturn(false);
        when(mockMessage.getReplyToMessage()).thenReturn(mockReplyToMessage);

        TelegramBridge.config.sendToChat = true;

        MessageListener messageListener = new MessageListener(mockTelegramBot);
        messageListener.onUpdateReceived(mockUpdate);

        BaseComponent finalComponent = new TextComponent("");
        finalComponent.addExtra(new TextComponent("§7[§bTelegram§7] §f[testUser] test"));

        verify(mockPlayerNoReplyPermSpigot, times(1)).sendMessage(argThat(new ComponentMatcher(finalComponent)));

        TextComponent replyButtonComponent = new TextComponent(" [Reply]");
        replyButtonComponent.setColor(ChatColor.AQUA);
        replyButtonComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to reply")));
        replyButtonComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tbr asdf "));
        finalComponent.addExtra(replyButtonComponent);

        verify(mockPlayerSpigot, times(1)).sendMessage(argThat(new ComponentMatcher(finalComponent)));
        verify(mockPlayerNoReceivePermSpigot, never()).sendMessage(any(BaseComponent.class));

        verify(mockMessage, times(2)).getText();
    }

    @Test
    @DisplayName("Reply")
    void reply() {
        when(mockMessage.getText()).thenReturn("test");
        when(mockReplyToMessage.getText()).thenReturn("test reply");
        when(mockReplyToMessage.hasText()).thenReturn(true);
        when(mockMessage.getReplyToMessage()).thenReturn(mockReplyToMessage);
        when(mockUser.getUserName()).thenReturn("testUser");

        TelegramBridge.config.sendToChat = true;

        MessageListener messageListener = new MessageListener(mockTelegramBot);
        messageListener.onUpdateReceived(mockUpdate);

        messageListener.reply("asdf", "TEST MESSAGE");

        BaseComponent finalComponent = new TextComponent("");
        TextComponent replyComponent = new TextComponent("- §breply to §7testUser: test...§r -\n");
        replyComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("test")));
        finalComponent.addExtra(replyComponent);
        finalComponent.addExtra(new TextComponent("TEST MESSAGE"));

        verify(mockPlayerNoReplyPermSpigot, times(1)).sendMessage(argThat(new ComponentMatcher(finalComponent)));
        verify(mockPlayerNoReplyPermSpigot, times(2)).sendMessage(any(BaseComponent.class));
        verify(mockPlayerNoReplyPerm, times(2)).hasPermission("telegrambridge.receive");
    }

    @Test
    @DisplayName("Reply no message to reply to")
    void replyNoMessageToReplyTo() {
        when(mockMessage.getText()).thenReturn("test");
        when(mockReplyToMessage.getText()).thenReturn("test reply");
        when(mockReplyToMessage.hasText()).thenReturn(true);
        when(mockMessage.getReplyToMessage()).thenReturn(mockReplyToMessage);
        when(mockUser.getUserName()).thenReturn("testUser");

        TelegramBridge.config.sendToChat = true;

        MessageListener messageListener = new MessageListener(mockTelegramBot);
        messageListener.onUpdateReceived(mockUpdate);

        messageListener.reply("dddd", "TEST MESSAGE");

        BaseComponent finalComponent = new TextComponent("");
        TextComponent replyComponent = new TextComponent("- §breply to §7testUser: test...§r -\n");
        replyComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("test")));
        finalComponent.addExtra(replyComponent);
        finalComponent.addExtra(new TextComponent("TEST MESSAGE"));

        verify(mockPlayerNoReplyPermSpigot, never()).sendMessage(argThat(new ComponentMatcher(finalComponent)));
        verify(mockPlayerNoReplyPermSpigot, times(1)).sendMessage(any(BaseComponent.class));
        verify(mockPlayerNoReplyPerm, times(1)).hasPermission("telegrambridge.receive");
    }
}
