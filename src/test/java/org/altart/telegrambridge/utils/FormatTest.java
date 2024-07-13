package org.altart.telegrambridge.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class FormatTest {
    @Test
    @DisplayName("Format string method")
    void string() {
        assertEquals("Hello, world!", Format.string("Hello, %name%!", "name", "world"));
        assertEquals("\n", Format.string("\\n", "name", "world"));
        HashMap<String, String> values = new HashMap<>();
        values.put("name", "world");
        assertEquals("Hello, world!", Format.string("Hello, %name%!", values));
    }
}
