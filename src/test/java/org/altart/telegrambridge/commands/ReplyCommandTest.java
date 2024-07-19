package org.altart.telegrambridge.commands;

import org.altart.telegrambridge.StandardMockTest;
import org.altart.telegrambridge.TelegramBridge;
import org.altart.telegrambridge.bot.TelegramBot;
import org.altart.telegrambridge.bot.feature.MessageListener;
import org.altart.telegrambridge.config.Config;
import org.altart.telegrambridge.config.Translations;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReplyCommandTest extends StandardMockTest {
    @Mock
    CommandSender sender;
    @Mock
    Plugin mockPlugin;
    @Mock
    MessageListener mockMessageListener;

    @Test
    void onCommand() {
        File translationsFolder = new File("src/test/resources");
        when(mockPlugin.getDataFolder()).thenReturn(translationsFolder);

        TelegramBridge.plugin = mockPlugin;
        TelegramBridge.config = new Config();
        TelegramBridge.telegramBot = new TelegramBot(mockPlugin);
        TelegramBridge.telegramBot.messageListenerFeature = mockMessageListener;
        TelegramBridge.translations = new Translations("en");

        when(sender.getName()).thenReturn("playerName");

        ReplyCommand replyCommand = new ReplyCommand();
        replyCommand.onCommand(sender, null, null, new String[]{"uuid", "message", "message"});
        verify(sender, times(1)).getName();
        verify(mockMessageListener, times(1)).reply("uuid", "[playerName]: message message");
    }

    @Test
    void onCommandWithEmptyArgs() {
        ReplyCommand replyCommand = new ReplyCommand();
        assertFalse(replyCommand.onCommand(sender, null, null, new String[]{}));
    }
}
