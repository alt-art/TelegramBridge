package org.altart.telegrambridge.bot.feature;

import org.altart.telegrambridge.Permissions;
import org.altart.telegrambridge.TelegramBridge;
import org.altart.telegrambridge.bot.TelegramBot;
import org.altart.telegrambridge.bot.TelegramFeature;
import org.altart.telegrambridge.utils.Format;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;

public class SentMedia extends TelegramFeature {

    public SentMedia(TelegramBot telegramBot) {
        super(telegramBot);
    }

    @Override
    public void onUpdateReceived(@NotNull Update update) {
        if (TelegramBridge.config.sendToChat) {
            Message message = update.getMessage();
            if (isMedia(message)) {
                String username = message.getFrom().getUserName();
                String caption = message.getCaption();
                caption = caption == null ? "" : "\n" + caption;
                HashMap<String, String> values = new HashMap<>();
                values.put("caption", caption);
                values.put("user", username);
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.hasPermission(Permissions.RECEIVE.getString())) {
                        String lang = TelegramBridge.database.getLang(player.getUniqueId());
                        values.put("type", determineMediaType(message, lang));
                        String text = Format.string(TelegramBridge.translations.get(lang).telegramMedia, values);
                        player.sendMessage(text);
                    }
                }
            }
        }
    }

    private boolean isMedia(@NotNull Message message) {
        return message.hasPhoto() || message.hasVideo() || message.hasDocument() || message.hasAudio() || message.hasVoice() || message.hasSticker() || message.hasContact() || message.hasLocation() || message.hasPoll();
    }

    private String determineMediaType(@NotNull Message message, @Nullable String lang) {
        if (message.hasPhoto()) return TelegramBridge.translations.get(lang).mediaTypes.get(0);
        if (message.hasVideo()) return TelegramBridge.translations.get(lang).mediaTypes.get(1);
        if (message.hasDocument()) return TelegramBridge.translations.get(lang).mediaTypes.get(2);
        if (message.hasAudio()) return TelegramBridge.translations.get(lang).mediaTypes.get(3);
        if (message.hasVoice()) return TelegramBridge.translations.get(lang).mediaTypes.get(4);
        if (message.hasSticker()) return TelegramBridge.translations.get(lang).mediaTypes.get(5);
        if (message.hasContact()) return TelegramBridge.translations.get(lang).mediaTypes.get(6);
        if (message.hasLocation()) return TelegramBridge.translations.get(lang).mediaTypes.get(7);
        if (message.hasPoll()) return TelegramBridge.translations.get(lang).mediaTypes.get(8);
        return TelegramBridge.translations.get(lang).mediaTypes.get(9);
    }
}
