package org.altart.telegrambridge.bot.feature;

import org.altart.telegrambridge.StandardMockTest;
import org.altart.telegrambridge.bot.TelegramBot;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserAutocompleteTest extends StandardMockTest {
    @Mock
    Update mockUpdate;
    @Mock
    TelegramBot mockTelegramBot;
    @Mock
    Message mockMessage;
    @Mock
    User mockUser;

    @Test
    @DisplayName("Get telegram users")
    void getTelegramUsers() {
        UserAutocomplete userAutocomplete = new UserAutocomplete(mockTelegramBot);
        when(mockMessage.getFrom()).thenReturn(mockUser);
        when(mockUser.getUserName()).thenReturn("testUser");
        when(mockUpdate.getMessage()).thenReturn(mockMessage);
        userAutocomplete.onUpdateReceived(mockUpdate);
        assertEquals(1, userAutocomplete.getTelegramUsers().size());
        assertEquals("@testUser", userAutocomplete.getTelegramUsers().get(0));
    }

    @Test
    @DisplayName("Get telegram users with null user")
    void nullUserName() {
        UserAutocomplete userAutocomplete = new UserAutocomplete(mockTelegramBot);
        when(mockMessage.getFrom()).thenReturn(mockUser);
        when(mockUser.getUserName()).thenReturn(null);
        when(mockUpdate.getMessage()).thenReturn(mockMessage);
        userAutocomplete.onUpdateReceived(mockUpdate);
        assertEquals(0, userAutocomplete.getTelegramUsers().size());
    }
}
