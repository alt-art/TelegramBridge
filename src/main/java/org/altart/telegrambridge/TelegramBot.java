package org.altart.telegrambridge;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.altart.telegrambridge.utils.Format;
import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.generics.BotSession;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.*;

public class TelegramBot {
    private BotMessageHandler botMessageHandler;
    private BotSession botSession;
    private final LinkedHashMap<String, MessageInfo> messagesInfo = new LinkedHashMap<>() {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, MessageInfo> _eldest) {
            return size() > 100;
        }
    };
    private final Set<String> telegramUsers = new HashSet<>();

    public TelegramBot(Plugin plugin) {
        try {
            botMessageHandler = new BotMessageHandler(plugin);
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            botSession = telegramBotsApi.registerBot(botMessageHandler);
            TelegramBridge.log.info("Telegram bot registered");
            botMessageHandler.setOnMessageCallback(this::onMessage);
        } catch (Exception e) {
            TelegramBridge.log.severe("Error registering bot: " + e.getMessage());
        }
    }

    public void send(String message) {
        if (botMessageHandler == null) {
            return;
        }
        botMessageHandler.broadcastMessage(message);
    }

    public void reply(String uuid, String text) {
        if (botMessageHandler == null) {
            return;
        }
        MessageInfo messageInfo = messagesInfo.get(uuid);
        if (messageInfo == null) {
            return;
        }
        String chatId = messageInfo.chatId;
        Integer messageId = messageInfo.messageId;
        botMessageHandler.sendMessage(text, chatId, null, messageId);
    }

    public void onMessage(Message message) {
        String username = message.getFrom().getUserName();
        telegramUsers.add("@" + message.getFrom().getUserName());
        if (!TelegramBridge.config.send_to_chat) return;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission(Permissions.RECEIVE.getString())) {
                HashMap<String, String> values = new HashMap<>();
                values.put("user", username);
                values.put("message", message.getText());
                String text = Format.string(TelegramBridge.config.messages_format_chat, values);
                ComponentBuilder componentBuilder = new ComponentBuilder(text);
                if (player.hasPermission(Permissions.REPLY_COMMAND.getString())) {
                    Integer messageId = message.getMessageId();
                    String chatId = message.getChatId().toString();
                    String uuid = RandomStringUtils.random(8, true, true);
                    while (messagesInfo.containsKey(uuid)) {
                        uuid = RandomStringUtils.random(8, true, true);
                    }
                    messagesInfo.put(uuid, new MessageInfo(chatId, messageId));
                    componentBuilder.append(" [Reply]").color(ChatColor.AQUA).event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tbr " + uuid + " ")).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to reply")));
                }
                player.spigot().sendMessage(componentBuilder.create());
            }
        }
    }

    public void stop() {
        botSession.stop();
    }

    public List<String> getTelegramUsers() {
        return new ArrayList<>(telegramUsers);
    }

    private static class MessageInfo {
        public String chatId;
        public Integer messageId;

        public MessageInfo(String chatId, Integer messageId) {
            this.chatId = chatId;
            this.messageId = messageId;
        }
    }
}
