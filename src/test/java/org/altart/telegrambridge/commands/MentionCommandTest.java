package org.altart.telegrambridge.commands;

import org.altart.telegrambridge.StandardMockTest;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MentionCommandTest extends StandardMockTest {
    @Mock
    Player player;

    @Test
    void onCommand() {
        MentionCommand mentionCommand = new MentionCommand();
        String[] args = {"Hello", "World"};
        assertTrue(mentionCommand.onCommand(player, null, null, args));
        verify(player).chat("Hello World");
    }

    @Test
    void onCommandNoArgs() {
        MentionCommand mentionCommand = new MentionCommand();
        String[] args = {};
        assertFalse(mentionCommand.onCommand(player, null, null, args));
    }

    @Test
    void onCommandNotPlayer() {
        MentionCommand mentionCommand = new MentionCommand();
        String[] args = {"Hello", "World"};
        assertFalse(mentionCommand.onCommand(null, null, null, args));
    }
}
