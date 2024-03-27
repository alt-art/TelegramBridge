package org.altart.telegrambridge.bot;

abstract public class TelegramCommandExecutor {
    public boolean requirePermission;
    public TelegramCommandExecutor(boolean requirePermission) {
        this.requirePermission = requirePermission;
    }
    public void onCommand(TelegramBot.CommandSender sender, String[] args) {}
}
