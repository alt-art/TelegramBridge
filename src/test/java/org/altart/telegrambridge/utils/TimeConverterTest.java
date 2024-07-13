package org.altart.telegrambridge.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TimeConverterTest {

    @Test
    @DisplayName("TimeConverter ticksToDays method")
    void daysToMonthsAndYears() {
        int[] oneDay = TimeConverter.daysToMonthsAndYears(1);
        assertEquals(1, oneDay[0]);
        assertEquals(0, oneDay[1]);
        assertEquals(0, oneDay[2]);

        int[] oneMonth = TimeConverter.daysToMonthsAndYears(32);
        assertEquals(1, oneMonth[0]);
        assertEquals(1, oneMonth[1]);
        assertEquals(0, oneMonth[2]);

        int[] twelveMonths = TimeConverter.daysToMonthsAndYears(365);
        assertEquals(30, twelveMonths[0]);
        assertEquals(11, twelveMonths[1]);
        assertEquals(0, twelveMonths[2]);

        int[] oneYear = TimeConverter.daysToMonthsAndYears(367);
        assertEquals(1, oneYear[0]);
        assertEquals(0, oneYear[1]);
        assertEquals(1, oneYear[2]);

        int[] fourtyYears = TimeConverter.daysToMonthsAndYears(14611);
        assertEquals(1, fourtyYears[0]);
        assertEquals(0, fourtyYears[1]);
        assertEquals(40, fourtyYears[2]);
    }

    @Test
    @DisplayName("TimeConverter ticksToTime method")
    void ticksToTime() {
        String zero = TimeConverter.ticksToTime(0);
        assertEquals("00:00", zero);
        String midday = TimeConverter.ticksToTime(6000);
        assertEquals("06:00", midday);
        String midnight = TimeConverter.ticksToTime(18000);
        assertEquals("18:00", midnight);
        String end = TimeConverter.ticksToTime(24000);
        assertEquals("24:00", end);
    }

    @Test
    @DisplayName("TimeConverter getTimeCycleEmoji method")
    void getTimeCycleEmoji() {
        String dawn = TimeConverter.getTimeCycleEmoji(0);
        assertEquals("\uD83C\uDF05", dawn);
        String morning = TimeConverter.getTimeCycleEmoji(12000);
        assertEquals("\uD83C\uDF07", morning);
        String noon = TimeConverter.getTimeCycleEmoji(13000);
        assertEquals("\uD83C\uDF03", noon);
        String evening = TimeConverter.getTimeCycleEmoji(23000);
        assertEquals("\uD83C\uDF05", evening);
    }
}
