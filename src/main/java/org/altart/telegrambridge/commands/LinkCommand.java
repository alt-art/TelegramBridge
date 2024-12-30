package org.altart.telegrambridge.commands;

import org.altart.telegrambridge.auth.AuthManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class LinkCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command _command, @NotNull String _label, String[] _args) {
        // cast sender to player
        if (sender instanceof Player) {
            AuthManager.addSession((Player) sender);
        }
        return true;
    }
}
