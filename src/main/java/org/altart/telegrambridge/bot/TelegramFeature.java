package org.altart.telegrambridge.bot;

import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.meta.api.objects.Update;

public abstract class TelegramFeature {
    public static TelegramBot telegramBot;

    public TelegramFeature(TelegramBot telegramBot) {
        TelegramFeature.telegramBot = telegramBot;
    }

    public void onUpdateReceived(@NotNull Update update) {
    }
}
