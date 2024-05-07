package org.altart.telegrambridge.commands;

import org.altart.telegrambridge.Permissions;
import org.altart.telegrambridge.TelegramBridge;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ConfigTabCompletion implements TabCompleter {
    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            if (commandSender.hasPermission(Permissions.DEFAULT_TRANSLATION_CONFIG.getString())) {
                completions.add("default-lang");
            }
            return completions;
        }
        if (args[0].equals("default-lang") && commandSender.hasPermission(Permissions.DEFAULT_TRANSLATION_CONFIG.getString())) {
            return TelegramBridge.translations.getLoadedLanguages();
        }
        return null;
    }
}
