package org.altart.telegrambridge.bot.feature;

import org.altart.telegrambridge.bot.TelegramBot;
import org.altart.telegrambridge.bot.TelegramFeature;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserAutocomplete extends TelegramFeature {
    public UserAutocomplete(TelegramBot telegramBot) {
        super(telegramBot);
    }

    private final Set<String> telegramUsers = new HashSet<>();

    public List<String> getTelegramUsers() {
        return new ArrayList<>(telegramUsers);
    }

    @Override
    public void onUpdateReceived(@NotNull Update update) {
        Message message = update.getMessage();
        if (message.getFrom().getUserName() != null) {
            telegramUsers.add("@" + message.getFrom().getUserName());
        }
    }
}
