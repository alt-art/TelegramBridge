package org.altart.telegrambridge.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MentionCommand implements CommandExecutor {
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length < 1) {
            return false;
        }
        if (sender instanceof Player player) {
            String message = String.join(" ", args);
            player.chat(message);
            return true;
        }
        return false;
    }
}
