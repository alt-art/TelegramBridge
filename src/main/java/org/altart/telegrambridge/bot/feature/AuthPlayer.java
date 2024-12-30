package org.altart.telegrambridge.bot.feature;

import org.altart.telegrambridge.TelegramBridge;
import org.altart.telegrambridge.auth.AuthManager;
import org.altart.telegrambridge.bot.TelegramBot;
import org.altart.telegrambridge.bot.TelegramFeature;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

public class AuthPlayer extends TelegramFeature {

    public AuthPlayer(TelegramBot telegramBot) {
        super(telegramBot);
    }

    @Override
    public void onUpdateReceived(@NotNull Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            if (message.hasText()) {
                String text = message.getText();
                if (text.startsWith("link-")) {
                    String code = update.getMessage().getText().substring(5).toLowerCase();
                    Long telegramId = update.getMessage().getFrom().getId();
                    AuthManager.login(code, telegramId);
                    TelegramBridge.telegramBot.deleteMessage(String.valueOf(message.getChatId()), message.getMessageId());
                }
            }
        }
    }
}
