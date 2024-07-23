package org.altart.telegrambridge.bot.feature;

import org.altart.telegrambridge.StandardMockTest;
import org.altart.telegrambridge.TelegramBridge;
import org.altart.telegrambridge.bot.TelegramBot;
import org.altart.telegrambridge.config.Config;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PinMessageTest extends StandardMockTest {

    @Mock
    TelegramBot mockTelegramBot;

    @Test
    @DisplayName("Build pinned message")
    void buildPinnedMessage() {
        TelegramBridge.config = mock(Config.class);
        TelegramBridge.config.pinned = "Players: %players%\nCount: %count%";
        doNothing().when(mockTelegramBot).editSystemMessage(anyString(), anyString(), anyInt());

        ArrayList<Config.Chat> chatArrayList = new ArrayList<>();
        chatArrayList.add(new Config.Chat("id", 1, 1));
        chatArrayList.add(new Config.Chat("id2", 2, null));
        TelegramBridge.config.chats = chatArrayList;

        PinMessage pinMessage = new PinMessage(mockTelegramBot);
        pinMessage.addPlayer("Player1");
        verify(mockTelegramBot, times(1)).editSystemMessage("Players: \nPlayer1\nCount: 1", "id", 1);
        pinMessage.addPlayer("Player2");
        verify(mockTelegramBot, times(1)).editSystemMessage("Players: \nPlayer2\nPlayer1\nCount: 2", "id", 1);
        assertEquals("Players: \nPlayer2\nPlayer1\nCount: 2", PinMessage.buildPinnedMessage());

        pinMessage.removePlayer("Player1");
        verify(mockTelegramBot, times(1)).editSystemMessage("Players: \nPlayer2\nCount: 1", "id", 1);
        assertEquals("Players: \nPlayer2\nCount: 1", PinMessage.buildPinnedMessage());

        pinMessage.removePlayer("Player2");
        verify(mockTelegramBot, times(1)).editSystemMessage("Players: \nCount: 0", "id", 1);
        assertEquals("Players: \nCount: 0", PinMessage.buildPinnedMessage());

        verify(mockTelegramBot, times(4)).editSystemMessage(anyString(), anyString(), anyInt());
    }
}
