package org.altart.telegrambridge;

import org.altart.telegrambridge.utils.Format;
import org.altart.telegrambridge.utils.TimeConverter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatAdministrators;
import org.telegram.telegrambots.meta.api.methods.pinnedmessages.PinChatMessage;
import org.telegram.telegrambots.meta.api.methods.pinnedmessages.UnpinChatMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;


public class BotMessageHandler extends TelegramLongPollingBot {
    private final Logger log = TelegramBridge.log;
    private final Config config;
    private final Plugin plugin;
    private OnMessageCallback onMessageCallback;
    private OnMediaCallback onMediaCallback;

    public BotMessageHandler(Plugin plugin) {
        super(Config.getInstance().getBotToken());
        this.config = Config.getInstance();
        this.plugin = plugin;
    }

    @Override
    public void onUpdateReceived(Update update) {
        List<Config.Chat> chats = config.getChats();
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

            HashMap<String, String> values = makeTimeMap(time, emoji, day, month, year);

            String response = Format.string(config.getMessagesFormatTime(), values);
            sendMessage(response, messageChatId, null, messageId);
        }

        if (message.getText().startsWith("/online")) {
            Collection<? extends Player> players = plugin.getServer().getOnlinePlayers();
            String playersNames = players.stream().map(Player::getDisplayName).collect(Collectors.joining("\n"));
            HashMap<String, String> values = new HashMap<>();
            values.put("players", players.isEmpty() ? "" : "\n" + playersNames);
            values.put("count", String.valueOf(players.size()));
            String response = Format.string(config.getMessagesFormatOnline(), values);
            sendMessage(response, messageChatId, null, messageId);
        }

        if (message.getText().startsWith("/setthread")) {
            if (isNotAdmin(messageChatId, message.getFrom().getId())) {
                sendMessage("You are not an admin", messageChatId, null, messageId);
                return;
            }
            config.setThread(messageChatId, message.getMessageThreadId());
            sendMessage("Thread set", messageChatId, null, messageId);
        }

        if (message.getText().startsWith("/setpin")) {
            if (isNotAdmin(messageChatId, message.getFrom().getId())) {
                sendMessage("You are not an admin", messageChatId, null, messageId);
                return;
            }
            TelegramBridge.telegramBot.setPinMessage(messageChatId, null, message.getMessageThreadId());
        }

        if (message.getText().startsWith("/unsetpin")) {
            if (isNotAdmin(messageChatId, message.getFrom().getId())) {
                sendMessage("You are not an admin", messageChatId, null, messageId);
                return;
            }
            TelegramBridge.telegramBot.unsetPinMessage(messageChatId);
        }
    }

    public HashMap<String, String> makeTimeMap(String time, String emoji, int day, int month, int year) {
        HashMap<String, String> values = new HashMap<>();
        values.put("time", time);
        values.put("emoji", emoji);
        values.put("day", String.valueOf(day));
        values.put("month", config.getMonths().get(month));
        values.put("year", String.valueOf(year));
        values.put("month_number", String.valueOf(month + 1));
        return values;
    }

    private boolean isNotAdmin(String chatId, Long userId) {
        GetChatAdministrators getChatAdministrators = new GetChatAdministrators();
        getChatAdministrators.setChatId(chatId);
        try {
            List<ChatMember> chatAdministrators = execute(getChatAdministrators);
            return chatAdministrators.stream().noneMatch(chatMember -> chatMember.getUser().getId().equals(userId));
        } catch (TelegramApiException e) {
            log.severe("Error getting chat administrators: " + e.getMessage());
            return true;
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

    public Message sendMessage(String message, String chatId, @Nullable Integer threadId, @Nullable Integer replyMessageId) {
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
            return execute(sendMessage);
        } catch (TelegramApiException e) {
            log.severe("Error sending message: " + e.getMessage());
            return null;
        }
    }

    public void broadcastMessage(String message) {
        List<Config.Chat> chats = config.getChats();
        for (Config.Chat chat : chats) {
            sendMessage(message, chat.id, chat.thread, null);
        }
    }

    public void pinMessage(String chatId, Integer messageId) {
        try {
            execute(new PinChatMessage(chatId, messageId));
        } catch (TelegramApiException e) {
            log.severe("Error pinning message: " + e.getMessage());
        }
    }

    public void unpinMessage(String chatId, Integer messageId) {
        try {
            execute(new UnpinChatMessage(chatId, messageId));
        } catch (TelegramApiException e) {
            log.severe("Error unpinning message: " + e.getMessage());
        }
    }

    public void editMessage(String message, String chatId, Integer messageId) {
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setText(message);
        editMessageText.setChatId(chatId);
        editMessageText.setMessageId(messageId);
        editMessageText.setParseMode("HTML");
        try {
            execute(editMessageText);
        } catch (TelegramApiException e) {
            log.severe("Error editing message: " + e.getMessage());
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
