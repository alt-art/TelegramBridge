package org.altart.telegrambridge.bot.commands;

import org.altart.telegrambridge.TelegramBridge;
import org.altart.telegrambridge.bot.TelegramBot;
import org.altart.telegrambridge.bot.TelegramCommandExecutor;
import org.altart.telegrambridge.utils.Format;
import org.altart.telegrambridge.utils.TimeConverter;

import java.util.HashMap;

public class TimeCommand extends TelegramCommandExecutor {
    public TimeCommand() {
        super(false);
    }

    @Override
    public void onCommand(TelegramBot.CommandSender sender, String[] args) {
        long ticks = sender.plugin.getServer().getWorlds().get(0).getTime();
        String time = TimeConverter.ticksToTime(ticks);
        long fullTicks = sender.plugin.getServer().getWorlds().get(0).getFullTime();
        long days = fullTicks / 24000;
        int[] daysMonthsYears = TimeConverter.daysToMonthsAndYears(days);
        int day = daysMonthsYears[0];
        int month = daysMonthsYears[1];
        int year = daysMonthsYears[2];
        String emoji = TimeConverter.getTimeCycleEmoji(ticks);

        HashMap<String, String> values = makeTimeMap(time, emoji, day, month, year);

        String response = Format.string(TelegramBridge.translations.time, values);
        sender.sendMessage(response);
    }

    public HashMap<String, String> makeTimeMap(String time, String emoji, int day, int month, int year) {
        HashMap<String, String> values = new HashMap<>();
        values.put("time", time);
        values.put("emoji", emoji);
        values.put("day", String.valueOf(day));
        values.put("month", TelegramBridge.translations.months[month]);
        values.put("year", String.valueOf(year));
        values.put("month_number", String.valueOf(month + 1));
        return values;
    }
}
