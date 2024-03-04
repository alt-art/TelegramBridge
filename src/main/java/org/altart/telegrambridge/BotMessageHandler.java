package org.altart.telegrambridge;

import org.altart.telegrambridge.utils.Format;
import org.altart.telegrambridge.utils.TimeConverter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatAdministrators;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;


public class BotMessageHandler extends TelegramLongPollingBot {
    private final Logger log = TelegramBridge.log;
    private final Config config;
    private final Plugin plugin;
    private OnMessageCallback onMessageCallback;
    private OnMediaCallback onMediaCallback;

    public BotMessageHandler(Plugin plugin) {
        super(Config.getInstance().bot_token);
        this.config = Config.getInstance();
        this.plugin = plugin;
    }

    @Override
    public void onUpdateReceived(Update update) {
        List<Config.Chats> chats = config.chats;
        Message message = update.getMessage();
        if (message == null) return;
        String messageChatId = message.getChatId().toString();
        if (chats.stream().noneMatch(chat -> chat.id.equals(messageChatId))) return;
        if (message.getFrom().getIsBot()) return;

        if (isMedia(message)) {
            onMediaCallback.onMedia(message);
        }

        if (!message.hasText()) return;

        if (!message.getText().startsWith("/")) {
            onMessageCallback.onMessage(message);
        }

        Integer messageId = message.getMessageId();
        if (message.getText().startsWith("/time")) {
            long ticks = plugin.getServer().getWorlds().get(0).getTime();
            String time = TimeConverter.ticksToTime(ticks);
            long fullTicks = plugin.getServer().getWorlds().get(0).getFullTime();
            long days = fullTicks / 24000;
            int[] daysMonthsYears = TimeConverter.daysToMonthsAndYears(days);
            int day = daysMonthsYears[0];
            int month = daysMonthsYears[1];
            int year = daysMonthsYears[2];
            String emoji = TimeConverter.getTimeCycleEmoji(ticks);

            HashMap<String, String> values = makeTimeMap(time, emoji, day, month, year, month);

            String response = Format.string(config.messages_format_time, values);
            sendMessage(response, messageChatId, null, messageId);
        }

        if (message.getText().startsWith("/online")) {
            Collection<? extends Player> players = plugin.getServer().getOnlinePlayers();
            String playersNames = players.stream().map(Player::getDisplayName).collect(Collectors.joining("\n"));
            HashMap<String, String> values = new HashMap<>();
            values.put("players", players.isEmpty() ? "" : "\n" + playersNames);
            values.put("count", String.valueOf(players.size()));
            String response = Format.string(config.messages_format_online, values);
            sendMessage(response, messageChatId, null, messageId);
        }

        if (message.getText().startsWith("/setthread")) {
            if (isAdmin(messageChatId, message.getFrom().getId())) {
                sendMessage("You are not an admin", messageChatId, null, messageId);
                return;
            }
            config.setThread(messageChatId, message.getMessageThreadId());
            sendMessage("Thread set", messageChatId, null, messageId);
        }
    }

    public HashMap<String, String> makeTimeMap(String time, String emoji, int day, int month, int year, int month_number) {
        HashMap<String, String> values = new HashMap<>();
        values.put("time", time);
        values.put("emoji", emoji);
        values.put("day", String.valueOf(day));
        values.put("month", config.months.get(month));
        values.put("year", String.valueOf(year));
        values.put("month_number", String.valueOf(month_number + 1));
        return values;
    }

    private boolean isAdmin(String chatId, long userId) {
        GetChatAdministrators getChatAdministrators = new GetChatAdministrators();
        getChatAdministrators.setChatId(chatId);
        try {
            List<ChatMember> chatAdministrators = execute(getChatAdministrators);
            return chatAdministrators.stream().anyMatch(chatMember -> chatMember.getUser().getId().equals(userId));
        } catch (TelegramApiException e) {
            log.severe("Error getting chat administrators: " + e.getMessage());
            return false;
        }
    }

    private boolean isMedia(Message message) {
        return message.hasPhoto()
                || message.hasVideo()
                || message.hasDocument()
                || message.hasAudio()
                || message.hasVoice()
                || message.hasSticker()
                || message.hasContact()
                || message.hasLocation()
                || message.hasPoll();
    }

    @Override
    public String getBotUsername() {
        return "TelegramBridgeBot";
    }

    public void sendMessage(String message, String chatId, @Nullable Integer threadId, @Nullable Integer replyMessageId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(message);
        sendMessage.setParseMode("HTML");
        sendMessage.setChatId(chatId);
        if (replyMessageId != null) {
            sendMessage.setReplyToMessageId(replyMessageId);
        }
        if (threadId != null) {
            sendMessage.setMessageThreadId(threadId);
        }
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.severe("Error sending message: " + e.getMessage());
        }
    }

    public void broadcastMessage(String message) {
        List<Config.Chats> chats = config.chats;
        for (Config.Chats chat : chats) {
            sendMessage(message, chat.id, chat.thread, null);
        }
    }

    public void setOnMessageCallback(OnMessageCallback callback) {
        onMessageCallback = callback;
    }

    public void setOnMediaCallback(OnMediaCallback callback) {
        onMediaCallback = callback;
    }

    public interface OnMessageCallback {
        void onMessage(Message message);
    }

    public interface OnMediaCallback {
        void onMedia(Message message);
    }
}
