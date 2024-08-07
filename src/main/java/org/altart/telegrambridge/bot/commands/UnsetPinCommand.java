package org.altart.telegrambridge.bot.commands;

import org.altart.telegrambridge.config.Config;
import org.altart.telegrambridge.TelegramBridge;
import org.altart.telegrambridge.bot.TelegramBot;
import org.altart.telegrambridge.bot.TelegramCommandExecutor;

public class UnsetPinCommand extends TelegramCommandExecutor {
    public UnsetPinCommand() {
        super(true);
    }

    @Override
    public void onCommand(TelegramBot.CommandSender sender, String[] args) {
        String chatId = String.valueOf(sender.message.getChatId());
        for (Config.Chat chat : TelegramBridge.config.chats) {
            if (chat.id.equals(chatId)) {
                if (chat.pinnedMessageId != null) {
                    sender.bot.unpinMessage(chatId, chat.pinnedMessageId);
                    sender.bot.deleteMessage(chatId, chat.pinnedMessageId);
                }
                TelegramBridge.config.setPinnedMessageId(chatId, null);
            }
        }
    }
}
