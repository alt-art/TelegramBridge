package org.altart.telegrambridge;

public enum Permissions {
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
