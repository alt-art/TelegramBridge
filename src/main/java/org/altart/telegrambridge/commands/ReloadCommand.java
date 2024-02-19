package org.altart.telegrambridge.commands;

import org.altart.telegrambridge.Config;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import org.jetbrains.annotations.NotNull;

public class ReloadCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command _command, @NotNull String _label, @NotNull String[] _args) {
        Config.getInstance().load();
        sender.sendMessage("Config reloaded!");
        return true;
    }
}
