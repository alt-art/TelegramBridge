package org.altart.telegrambridge.bot.feature;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.altart.telegrambridge.Permissions;
import org.altart.telegrambridge.TelegramBridge;
import org.altart.telegrambridge.bot.TelegramBot;
import org.altart.telegrambridge.bot.TelegramFeature;
import org.altart.telegrambridge.utils.Format;
import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class MessageListener extends TelegramFeature {
    private final LinkedHashMap<String, MessageInfo> messagesInfo = new LinkedHashMap<>() {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, MessageInfo> _eldest) {
            return size() > 100;
        }
    };

    public MessageListener(TelegramBot telegramBot) {
        super(telegramBot);
    }

    public void reply(String uuid, String text) {
        MessageInfo messageInfo = messagesInfo.get(uuid);
        if (messageInfo == null) {
            return;
        }
        String chatId = messageInfo.chatId;
        Integer messageId = messageInfo.messageId;
        telegramBot.sendMessage(text, chatId, null, messageId);
        String username = messageInfo.username;
        String message = messageInfo.message;
        ComponentBuilder componentBuilder = new ComponentBuilder();
        HashMap<String, String> values = makeMessageMap(username, shrinkReplyText(message));
        componentBuilder.append(Format.string(TelegramBridge.config.getMessagesFormatReply(), values));
        componentBuilder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(message)));
        componentBuilder.append(text, ComponentBuilder.FormatRetention.NONE);
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission(Permissions.RECEIVE.getString())) {
                player.spigot().sendMessage(componentBuilder.create());
            }
        }
    }

    @Override
    public void onUpdateReceived(@NotNull Update update) {
        Message message = update.getMessage();
        String text = message.getText();
        if (text != null && !text.startsWith("/") && TelegramBridge.config.getSendToChat()) {
            String username = message.getFrom().getUserName();
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.hasPermission(Permissions.RECEIVE.getString())) {
                    HashMap<String, String> values = makeMessageMap(username, text);
                    Message replyToMessage = message.getReplyToMessage();
                    ComponentBuilder componentBuilder = new ComponentBuilder();
                    if (replyToMessage != null && replyToMessage.hasText()) {
                        String replyToUsername = replyToMessage.getFrom().getUserName();
                        String replyToText = replyToMessage.getText();
                        HashMap<String, String> replyValues = makeMessageMap(replyToUsername, shrinkReplyText(replyToText));
                        componentBuilder.append(Format.string(TelegramBridge.config.getMessagesFormatReply(), replyValues));
                        componentBuilder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(replyToText)));
                    }
                    componentBuilder.append(Format.string(TelegramBridge.config.getMessagesFormatChat(), values), ComponentBuilder.FormatRetention.NONE);
                    if (player.hasPermission(Permissions.REPLY_COMMAND.getString())) {
                        Integer messageId = message.getMessageId();
                        String chatId = message.getChatId().toString();
                        String uuid = RandomStringUtils.random(8, true, true);
                        while (messagesInfo.containsKey(uuid)) {
                            uuid = RandomStringUtils.random(8, true, true);
                        }
                        messagesInfo.put(uuid, new MessageInfo(chatId, messageId, message.getText(), username));
                        componentBuilder.append(" [Reply]").color(ChatColor.AQUA).event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tbr " + uuid + " ")).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to reply")));
                    }
                    player.spigot().sendMessage(componentBuilder.create());
                }
            }
        }
    }

    private static @NotNull HashMap<String, String> makeMessageMap(String user, String message) {
        HashMap<String, String> values = new HashMap<>();
        values.put("user", user);
        values.put("message", message);
        return values;
    }

    private static @NotNull String shrinkReplyText(@NotNull String text) {
        return text.replace("\n", " ").substring(0, Math.min(text.length(), 20)) + "...";
    }

    private static class MessageInfo {
        public String chatId;
        public Integer messageId;
        public String message;
        public String username;

        public MessageInfo(String chatId, Integer messageId, String message, String username) {
            this.chatId = chatId;
            this.messageId = messageId;
            this.message = message;
            this.username = username;
        }
    }
}
