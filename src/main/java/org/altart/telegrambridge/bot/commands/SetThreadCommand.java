package org.altart.telegrambridge.bot.commands;

import org.altart.telegrambridge.TelegramBridge;
import org.altart.telegrambridge.bot.TelegramBot;
import org.altart.telegrambridge.bot.TelegramCommandExecutor;

public class SetThreadCommand extends TelegramCommandExecutor {
    public SetThreadCommand() {
        super(true);
    }

    @Override
    public void onCommand(TelegramBot.CommandSender sender, String[] args) {
        TelegramBridge.config.setThread(String.valueOf(sender.message.getChatId()), sender.message.getMessageThreadId());
        sender.sendMessage("Thread set");
    }
}
