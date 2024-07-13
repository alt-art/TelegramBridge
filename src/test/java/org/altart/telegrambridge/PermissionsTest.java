package org.altart.telegrambridge;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PermissionsTest {

    static final String[] PERMISSIONS = {"DEFAULT_TRANSLATION_CONFIG", "TRANSLATION_CONFIG", "REPLY_COMMAND", "RECEIVE", "SEND"};

    @Test
    @DisplayName("Permissions getString method")
    void getString() {
        assertEquals("telegrambridge.commands.config.default-language", Permissions.DEFAULT_TRANSLATION_CONFIG.getString());
        assertEquals("telegrambridge.commands.config.language", Permissions.TRANSLATION_CONFIG.getString());
        assertEquals("telegrambridge.commands.reply", Permissions.REPLY_COMMAND.getString());
        assertEquals("telegrambridge.receive", Permissions.RECEIVE.getString());
        assertEquals("telegrambridge.send", Permissions.SEND.getString());
    }

    @Test
    @DisplayName("Permissions values method")
    void values() {
        Permissions[] perms = Permissions.values();
        for (int i = 0; i < perms.length; i++) {
            assertEquals(PERMISSIONS[i], perms[i].name());
        }
        assertEquals(PERMISSIONS.length, perms.length);
    }

    @Test
    @DisplayName("Permissions valueOf method")
    void valueOf() {
        for (String perm : PERMISSIONS) {
            assertEquals(perm, Permissions.valueOf(perm).name());
        }
    }
}
