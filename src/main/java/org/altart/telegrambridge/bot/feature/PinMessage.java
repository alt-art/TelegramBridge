package org.altart.telegrambridge.bot.feature;

import org.altart.telegrambridge.config.Config;
import org.altart.telegrambridge.TelegramBridge;
import org.altart.telegrambridge.bot.TelegramBot;
import org.altart.telegrambridge.bot.TelegramFeature;
import org.altart.telegrambridge.utils.Format;

import java.util.*;

public class PinMessage extends TelegramFeature {
    private static final Set<String> players = new HashSet<>();

    public PinMessage(TelegramBot telegramBot) {
        super(telegramBot);
        updatePinnedMessage();
    }

    static public String buildPinnedMessage() {
        String playersNames = String.join("\n", players);
        HashMap<String, String> values = new HashMap<>();
        values.put("players", players.isEmpty() ? "" : "\n" + playersNames);
        values.put("count", String.valueOf(players.size()));
        return Format.string(TelegramBridge.config.pinned, values);
    }

    public void addPlayer(String player) {
        players.add(player);
        updatePinnedMessage();
    }

    public void removePlayer(String player) {
        players.remove(player);
        updatePinnedMessage();
    }

    private void updatePinnedMessage() {
        List<Config.Chat> chats = TelegramBridge.config.chats;
        for (Config.Chat chat : chats) {
            if (chat.pinnedMessageId != null) {
                String chatId = chat.id;
                String pinnedMessageText = buildPinnedMessage();
                telegramBot.editSystemMessage(pinnedMessageText, chatId, chat.pinnedMessageId);
            }
        }
    }
}
