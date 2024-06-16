package org.altart.telegrambridge.bot.feature;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
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
import org.jetbrains.annotations.Nullable;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class MessageListener extends TelegramFeature {
    private final LinkedHashMap<String, MessageInfo> messagesInfo = new LinkedHashMap<String, MessageInfo>() {
        @Override
        public boolean removeEldestEntry(Map.Entry<String, MessageInfo> _eldest) {
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
        telegramBot.reply(text, chatId, messageId);
        String username = messageInfo.username;
        String message = messageInfo.message;
        for (Player player : Bukkit.getOnlinePlayers()) {
            TextComponent finalComponent = new TextComponent("");
            String lang = TelegramBridge.database.getLang(player.getUniqueId());
            finalComponent.addExtra(replyComponent(username, message, lang));
            TextComponent component = new TextComponent(text);
            finalComponent.addExtra(component);
            if (player.hasPermission(Permissions.RECEIVE.getString())) {
                player.spigot().sendMessage(finalComponent);
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onUpdateReceived(@NotNull Update update) {
        Message message = update.getMessage();
        String text = message.getText();
        if (text != null && !text.startsWith("/") && TelegramBridge.config.sendToChat) {
            String username = message.getFrom().getUserName();
            if (username == null) {
                username = message.getFrom().getFirstName();
            }
            TelegramBridge.log.info("Telegram message received from " + username + ": " + text);
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.hasPermission(Permissions.RECEIVE.getString())) {
                    BaseComponent finalComponent = new TextComponent("");
                    HashMap<String, String> values = makeMessageMap(username, text);

                    String lang = TelegramBridge.database.getLang(player.getUniqueId());

                    Message reply = message.getReplyToMessage();
                    if (reply != null && reply.hasText()) {
                        String replyUsername = reply.getFrom().getUserName();
                        String replyMessage = reply.getText();
                        finalComponent.addExtra(replyComponent(replyUsername, replyMessage, lang));
                    }

                    TextComponent component = new TextComponent(Format.string(TelegramBridge.translations.get(lang).telegramMessage, values));
                    finalComponent.addExtra(component);

                    if (player.hasPermission(Permissions.REPLY_COMMAND.getString())) {
                        Integer messageId = message.getMessageId();
                        String chatId = message.getChatId().toString();
                        String uuid = RandomStringUtils.random(8, true, true);
                        while (messagesInfo.containsKey(uuid)) {
                            uuid = RandomStringUtils.random(8, true, true);
                        }
                        messagesInfo.put(uuid, new MessageInfo(chatId, messageId, message.getText(), username));

                        TextComponent replyButtonComponent = new TextComponent(" [" + TelegramBridge.translations.get(lang).replyButton + "]");
                        replyButtonComponent.setColor(ChatColor.AQUA);
                        replyButtonComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tbr " + uuid + " "));

                        String replyHint = TelegramBridge.translations.get(lang).replyHint;
                        try {
                            Class.forName("net.md_5.bungee.api.chat.hover.content.Content");
                            replyButtonComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(replyHint)));
                        } catch (ClassNotFoundException ignored) {
                            replyButtonComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(replyHint).create()));
                        }

                        finalComponent.addExtra(replyButtonComponent);
                    }
                    player.spigot().sendMessage(finalComponent);
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
    private static TextComponent replyComponent(String username, String message, @Nullable String lang) {
        HashMap<String, String> replyValues = makeMessageMap(username, shrinkReplyText(message));
        TextComponent replyComponent = new TextComponent(Format.string(TelegramBridge.translations.get(lang).telegramReply, replyValues));
        try {
            Class.forName("net.md_5.bungee.api.chat.hover.content.Content");
            replyComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(message)));
        } catch (ClassNotFoundException ignored) {
            replyComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(message).create()));
        }
        return replyComponent;
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
