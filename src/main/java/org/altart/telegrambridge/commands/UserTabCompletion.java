package org.altart.telegrambridge.commands;

import org.altart.telegrambridge.TelegramBridge;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class UserTabCompletion implements TabCompleter {
    final int argLength;

    public UserTabCompletion(int argLength) {
        this.argLength = argLength;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length <= argLength) {
            return TelegramBridge.telegramBot.userAutocompleteFeature.getTelegramUsers();
        }
        return Collections.emptyList();
    }
}
