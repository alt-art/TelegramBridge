package org.altart.telegrambridge.commands;

import org.altart.telegrambridge.StandardMockTest;
import org.altart.telegrambridge.TelegramBridge;
import org.altart.telegrambridge.bot.TelegramBot;
import org.altart.telegrambridge.bot.feature.UserAutocomplete;
import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserTabCompletionTest extends StandardMockTest {
    @Mock
    CommandSender mockCommandSender;
    @Mock
    TelegramBot mockTelegramBot;
    @Mock
    UserAutocomplete mockUserAutocomplete;

    @Test
    void onTabComplete() {
        TelegramBridge.telegramBot = mockTelegramBot;
        TelegramBridge.telegramBot.userAutocompleteFeature = mockUserAutocomplete;

        UserTabCompletion userTabCompletion = new UserTabCompletion(1);
        String[] args = new String[]{"test"};

        List<String> users = new ArrayList<>();
        users.add("test1");
        users.add("test2");
        users.add("test3");
        when(mockUserAutocomplete.getTelegramUsers()).thenReturn(users);

        List<String> result = userTabCompletion.onTabComplete(mockCommandSender, null, null, args);
        assertEquals(3, result.size());
        assertEquals("test1", result.get(0));
        assertEquals("test2", result.get(1));
        assertEquals("test3", result.get(2));
    }

    @Test
    void onTabCompleteNoArgs() {
        TelegramBridge.telegramBot = mockTelegramBot;
        TelegramBridge.telegramBot.userAutocompleteFeature = mockUserAutocomplete;

        UserTabCompletion userTabCompletion = new UserTabCompletion(1);
        String[] args = {"test", "test2"};

        List<String> users = new ArrayList<>();
        users.add("test1");
        users.add("test2");
        users.add("test3");
        when(mockUserAutocomplete.getTelegramUsers()).thenReturn(users);

        List<String> result = userTabCompletion.onTabComplete(mockCommandSender, null, null, args);
        assertEquals(0, result.size());
    }
}
