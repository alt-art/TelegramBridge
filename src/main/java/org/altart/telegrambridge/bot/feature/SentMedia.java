package org.altart.telegrambridge.bot.feature;

import org.altart.telegrambridge.Permissions;
import org.altart.telegrambridge.TelegramBridge;
import org.altart.telegrambridge.bot.TelegramBot;
import org.altart.telegrambridge.bot.TelegramFeature;
import org.altart.telegrambridge.utils.Format;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;

public class SentMedia extends TelegramFeature {

    public SentMedia(TelegramBot telegramBot) {
        super(telegramBot);
    }

    @Override
    public void onUpdateReceived(@NotNull Update update) {

        Message message = update.getMessage();
        if (isMedia(message)) {
            String username = message.getFrom().getUserName();
            String caption = message.getCaption();
            caption = caption == null ? "" : "\n" + caption;
            if (TelegramBridge.config.sendToChat) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.hasPermission(Permissions.RECEIVE.getString())) {
                        HashMap<String, String> values = new HashMap<>();
                        values.put("user", username);
                        values.put("caption", caption);
                        values.put("type", determineMediaType(message));
                        String text = Format.string(TelegramBridge.translations.telegramMedia, values);
                        player.sendMessage(text);
                    }
                }
            }
        }
    }

    private boolean isMedia(@NotNull Message message) {
        return message.hasPhoto() || message.hasVideo() || message.hasDocument() || message.hasAudio() || message.hasVoice() || message.hasSticker() || message.hasContact() || message.hasLocation() || message.hasPoll();
    }

    private String determineMediaType(@NotNull Message message) {
        if (message.hasPhoto()) return TelegramBridge.translations.mediaTypes[0];
        if (message.hasVideo()) return TelegramBridge.translations.mediaTypes[1];
        if (message.hasDocument()) return TelegramBridge.translations.mediaTypes[2];
        if (message.hasAudio()) return TelegramBridge.translations.mediaTypes[3];
        if (message.hasVoice()) return TelegramBridge.translations.mediaTypes[4];
        if (message.hasSticker()) return TelegramBridge.translations.mediaTypes[5];
        if (message.hasContact()) return TelegramBridge.translations.mediaTypes[6];
        if (message.hasLocation()) return TelegramBridge.translations.mediaTypes[7];
        if (message.hasPoll()) return TelegramBridge.translations.mediaTypes[8];
        return TelegramBridge.translations.mediaTypes[9];
    }
}
