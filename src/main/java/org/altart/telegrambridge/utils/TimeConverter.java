package org.altart.telegrambridge.utils;

public class TimeConverter {
    public static int[] daysToMonthsAndYears(long days) {
        int[] daysInMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        long consumeDays = days;
        int month = 0;
        int year = 0;
        while (consumeDays > daysInMonth[month]) {
            consumeDays -= daysInMonth[month];
            if (month == 1) {
                if ((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)) {
                    consumeDays--;
                }
            }
            month++;
            if (month == 12) {
                month = 0;
                year++;
            }
        }
        return new int[]{(int) consumeDays, month, year};
    }

    public static String ticksToTime(long ticks) {
        long hh = ticks / 1000;
        long mm = (ticks % 1000) * 60 / 1000;
        return String.format("%02d:%02d", hh, mm);
    }

    public static String getTimeCycleEmoji(long ticks) {
        String emoji = "";
        if (0 <= ticks && ticks < 12000) {
            emoji = "\uD83C\uDF05";
        } else if (12000 <= ticks && ticks < 13000) {
            emoji = "\uD83C\uDF07🌇";
        } else if (13000 <= ticks && ticks < 23000) {
            emoji = "\uD83C\uDF03";
        } else if (23000 <= ticks && ticks < 24000) {
            emoji = "\uD83C\uDF05🌅";
        }
        return emoji;
    }
}
