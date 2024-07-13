package org.altart.telegrambridge.commands;

import org.altart.telegrambridge.Permissions;
import org.altart.telegrambridge.TelegramBridge;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ConfigCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command _command, @NotNull String _label, String[] args) {
        if (args.length < 1) {
            return false;
        }
        if (args[0].equals("default-lang")) {
            if (!sender.hasPermission(Permissions.DEFAULT_TRANSLATION_CONFIG.getString())) {
                sender.sendMessage("You do not have permission to use this command.");
                return false;
            }
            try {
                TelegramBridge.translations.setDefaultLang(args[1]);
                TelegramBridge.config.setLang(args[1]);
            } catch (Exception e) {
                sender.sendMessage(e.getMessage());
                return false;
            }
        }

        if (args[0].equals("lang")) {
            if (!sender.hasPermission(Permissions.TRANSLATION_CONFIG.getString())) {
                sender.sendMessage("You do not have permission to use this command.");
                return false;
            }
            try {
                if (sender instanceof Player) {
                    TelegramBridge.database.setLang(((Player) sender).getUniqueId(), args[1]);
                } else {
                    sender.sendMessage("This command can only be used by players.");
                    return false;
                }
            } catch (Exception e) {
                sender.sendMessage(e.getMessage());
                return false;
            }
        }
        return true;
    }
}
