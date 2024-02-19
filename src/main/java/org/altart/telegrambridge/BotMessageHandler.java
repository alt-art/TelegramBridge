package org.altart.telegrambridge;

import org.altart.telegrambridge.utils.Format;
import org.altart.telegrambridge.utils.TimeConverter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;


public class BotMessageHandler extends TelegramLongPollingBot {
    private final Config config;
    private final Plugin plugin;
    private static OnMessageCallback onMessageCallback;

    public BotMessageHandler(Plugin plugin) {
        super(Config.getInstance().bot_token);
        this.config = Config.getInstance();
        this.plugin = plugin;
    }

    @Override
    public void onUpdateReceived(Update update) {
        List<String> chatIds = config.chat_ids;
        if (!update.hasMessage() || !update.getMessage().hasText()) return;
        Message message = update.getMessage();
        if (message.hasText() && chatIds.contains(message.getChatId().toString()) && !message.getText().startsWith("/")) {
            onMessageCallback.onMessage(message);
        }

        if (message.getText().startsWith("/time") && chatIds.contains(message.getChatId().toString())) {
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
            sendMessage(response, message.getChatId().toString());
        }

        if (message.getText().startsWith("/online") && chatIds.contains(message.getChatId().toString())) {
            Collection<? extends Player> players = plugin.getServer().getOnlinePlayers();
            String playersNames = players.stream().map(Player::getDisplayName).collect(Collectors.joining("\n"));
            HashMap<String, String> values = new HashMap<>();
            values.put("players", playersNames);
            values.put("count", String.valueOf(players.size()));
            String response = Format.string(config.messages_format_online, values);
            sendMessage(response, message.getChatId().toString());
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

    @Override
    public String getBotUsername() {
        return "TelegramBridgeBot";
    }

    public void sendMessage(String message, String chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);
        try {
            execute(sendMessage);
        } catch (Exception e) {
            System.out.println("Error sending message: " + e.getMessage());
        }
    }

    public void broadcastMessage(String message) {
        List<String> chatIds = config.chat_ids;
        for (String chatId : chatIds) {
            sendMessage(message, chatId);
        }
    }

    public void setOnMessageCallback(OnMessageCallback callback) {
        onMessageCallback = callback;
    }

    public interface OnMessageCallback {
        void onMessage(Message message);
    }
}
