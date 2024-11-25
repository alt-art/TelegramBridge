package org.altart.telegrambridge.commands;

import org.altart.telegrambridge.TelegramBridge;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class LinkCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command _command, @NotNull String _label, String[] _args) {
        String code = TelegramBridge.telegramBot.linkAccountFeature.addSession(sender.getName());
        sender.sendMessage("Please send the following code to the telegram chat: #link-" + code);
        return true;
    }
}
