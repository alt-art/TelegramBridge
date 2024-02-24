package org.altart.telegrambridge.commands;

import org.altart.telegrambridge.TelegramBridge;
import org.altart.telegrambridge.utils.Format;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class MentionCommand implements CommandExecutor {
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length < 1) {
            return false;
        }
        String message = String.join(" ", args);
        String playerName = sender.getName();
        String text = Format.message(playerName, message);
        TelegramBridge.telegramBot.send(text);
        return true;
    }
}
