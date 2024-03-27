package org.altart.telegrambridge.bot.commands;

import org.altart.telegrambridge.TelegramBridge;
import org.altart.telegrambridge.bot.TelegramBot;
import org.altart.telegrambridge.bot.TelegramCommandExecutor;
import org.altart.telegrambridge.bot.feature.PinMessage;

public class SetPinCommand extends TelegramCommandExecutor {
    public SetPinCommand() {
        super(true);
    }

    @Override
    public void onCommand(TelegramBot.CommandSender sender, String[] args) {
        String chatId = String.valueOf(sender.message.getChatId());
        Integer threadId = sender.message.getMessageThreadId();
        String pinnedMessageText = PinMessage.buildPinnedMessage();
        Integer messageId = sender.bot.sendMessage(pinnedMessageText, chatId, threadId, null).getMessageId();
        sender.bot.pinMessage(chatId, messageId);
        if (messageId != null) {
            TelegramBridge.config.setPinnedMessageId(chatId, messageId);
        }
    }
}
