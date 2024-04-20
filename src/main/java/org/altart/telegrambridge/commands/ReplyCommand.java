package org.altart.telegrambridge.commands;

import org.altart.telegrambridge.TelegramBridge;
import org.altart.telegrambridge.utils.Format;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class ReplyCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String _label, @NotNull String[] args) {
        if (args.length < 2) {
            return false;
        }
        String uuid = args[0];
        String message = String.join(" ", args).substring(args[0].length() + 1);
        String playerName = sender.getName();
        HashMap<String, String> values = new HashMap<>();
        values.put("playername", playerName);
        values.put("message", message);
        String text = Format.string(TelegramBridge.translations.chatMessage, values);
        TelegramBridge.telegramBot.messageListenerFeature.reply(uuid, text);
        return true;
    }
}
