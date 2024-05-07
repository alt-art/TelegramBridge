package org.altart.telegrambridge;

public enum Permissions {
    DEFAULT_TRANSLATION_CONFIG("commands.config.default-language"),
    REPLY_COMMAND("commands.reply"),
    RECEIVE("receive"),
    SEND("send");

    private final String permission;

    Permissions(String permission) {
        this.permission = permission;
    }

    public String getString() {
        return "telegrambridge." + permission;
    }
}
