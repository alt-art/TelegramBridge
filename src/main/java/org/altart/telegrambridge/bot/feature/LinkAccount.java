package org.altart.telegrambridge.bot.feature;

import org.altart.telegrambridge.TelegramBridge;
import org.altart.telegrambridge.bot.TelegramBot;
import org.altart.telegrambridge.bot.TelegramFeature;
import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.entity.Player;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Collection;
import java.util.HashMap;

public class LinkAccount extends TelegramFeature {
    private final HashMap<String, String> sessions = new HashMap<>();

    public LinkAccount(TelegramBot telegramBot) {
        super(telegramBot);
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText() && update.getMessage().getText().startsWith("#link-")) {
            String code = update.getMessage().getText().substring(6);
            Long telegram_id = update.getMessage().getFrom().getId();
            if (sessions.containsKey(code)) {
                String nickname = sessions.get(code);
                sessions.remove(code);
                TelegramBridge.database.linkAccount(nickname, telegram_id);
                telegramBot.reply("Account linked successfully!", update.getMessage().getChatId().toString(), update.getMessage().getMessageId());
            } else {
                telegramBot.reply("Invalid code!", update.getMessage().getChatId().toString(), update.getMessage().getMessageId());
            }
        }
    }

    public String addSession(String nickname) {
        purgeOldSessions();
        String code = RandomStringUtils.randomAlphanumeric(6).toUpperCase();
        sessions.put(code, nickname);
        return code;
    }

    private void purgeOldSessions() {
        Collection<? extends Player> players = TelegramBridge.plugin.getServer().getOnlinePlayers();
        sessions.entrySet().removeIf(entry -> players.stream().noneMatch(player -> player.getName().equals(entry.getKey())));
    }
}
