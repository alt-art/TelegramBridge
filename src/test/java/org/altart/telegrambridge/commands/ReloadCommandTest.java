package org.altart.telegrambridge.commands;

import org.altart.telegrambridge.StandardMockTest;
import org.altart.telegrambridge.TelegramBridge;
import org.altart.telegrambridge.config.Config;
import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReloadCommandTest extends StandardMockTest {
    @Mock
    CommandSender sender;

    @Mock
    Config config;

    @Test
    void onCommand() {
        TelegramBridge.config = config;

        ReloadCommand reloadCommand = new ReloadCommand();
        assertTrue(reloadCommand.onCommand(sender, null, null, null));
        verify(TelegramBridge.config).load();
        verify(sender).sendMessage("Config reloaded!");
    }
}
