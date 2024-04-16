package org.altart.telegrambridge.bot.commands;

import org.altart.telegrambridge.config.Config;
import org.altart.telegrambridge.TelegramBridge;
import org.altart.telegrambridge.bot.TelegramBot;
import org.altart.telegrambridge.bot.TelegramCommandExecutor;
import org.altart.telegrambridge.bot.feature.PinMessage;

import java.util.Optional;

public class SetPinCommand extends TelegramCommandExecutor {
    public SetPinCommand() {
        super(true);
    }

    @Override
    public void onCommand(TelegramBot.CommandSender sender, String[] args) {
        String chatId = String.valueOf(sender.message.getChatId());
        Optional<Config.Chat> chatConfig = TelegramBridge.config.getChats().stream().filter(chat -> chat.id.equals(chatId)).findFirst();
        chatConfig.ifPresent(chat -> {
            if (chat.pinnedMessageId != null) {
                sender.bot.unpinMessage(chatId, chat.pinnedMessageId);
                sender.bot.deleteMessage(chatId, chat.pinnedMessageId);
            }
        });
        Integer threadId = sender.message.getMessageThreadId();
        String pinnedMessageText = PinMessage.buildPinnedMessage();
        Integer messageId = sender.bot.sendSystemMessage(pinnedMessageText, chatId, threadId).getMessageId();
        sender.bot.pinMessage(chatId, messageId);
        if (messageId != null) {
            TelegramBridge.config.setPinnedMessageId(chatId, messageId);
        }
    }
}
