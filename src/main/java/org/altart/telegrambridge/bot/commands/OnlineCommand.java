package org.altart.telegrambridge.bot.commands;

import org.altart.telegrambridge.TelegramBridge;
import org.altart.telegrambridge.bot.TelegramBot;
import org.altart.telegrambridge.bot.TelegramCommandExecutor;
import org.altart.telegrambridge.utils.Format;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.stream.Collectors;

public class OnlineCommand extends TelegramCommandExecutor {
    public OnlineCommand() {
        super(false);
    }

    @Override
    public void onCommand(TelegramBot.CommandSender sender, String[] args) {
        Collection<? extends Player> players = sender.plugin.getServer().getOnlinePlayers();
        String playersNames = players.stream().map(Player::getDisplayName).collect(Collectors.joining("\n"));
        HashMap<String, String> values = new HashMap<>();
        values.put("players", players.isEmpty() ? "" : "\n" + playersNames);
        values.put("count", String.valueOf(players.size()));
        String response = Format.string(TelegramBridge.translations.online, values);
        sender.sendMessage(response);
    }
}
