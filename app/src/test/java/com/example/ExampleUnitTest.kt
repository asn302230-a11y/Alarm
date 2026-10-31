package com.example

import com.example.data.local.Converters
import com.example.data.model.AlarmItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

class ExampleUnitTest {
    @Test
    fun testAlarmFormatting() {
        val morningAlarm = AlarmItem(hour = 7, minute = 30)
        assertEquals("07:30", morningAlarm.formattedTime)
        assertEquals("7:30", morningAlarm.formatted12HourTime)
        assertEquals("AM", morningAlarm.amPm)

        val midnightAlarm = AlarmItem(hour = 0, minute = 5)
        assertEquals("12:05", midnightAlarm.formatted12HourTime)
        assertEquals("AM", midnightAlarm.amPm)

        val eveningAlarm = AlarmItem(hour = 21, minute = 45)
        assertEquals("9:45", eveningAlarm.formatted12HourTime)
        assertEquals("PM", eveningAlarm.amPm)
    }

    @Test
    fun testIraqiWeekendSummary() {
        val weekendAlarm = AlarmItem(
            hour = 8,
            minute = 0,
            daysOfWeek = listOf(Calendar.FRIDAY, Calendar.SATURDAY)
        )
        assertEquals("Weekends (Fri - Sat)", weekendAlarm.repeatSummary)
    }

    @Test
    fun testRoomConverters() {
        val converters = Converters()
        val originalList = listOf(1, 2, 5, 7)
        val serialized = converters.fromIntList(originalList)
        val deserialized = converters.toIntList(serialized)
        assertEquals(originalList, deserialized)
    }

    @Test
    fun testNextTriggerCalculation() {
        val now = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 10)
            set(Calendar.MINUTE, 0)
        }.timeInMillis

        // Alarm at 11:00 should be scheduled today
        val todayAlarm = AlarmItem(hour = 11, minute = 0)
        val triggerToday = todayAlarm.getNextTriggerMillis(now)
        assertTrue(triggerToday > now)
        assertTrue(triggerToday - now <= 24 * 3600 * 1000L)
    }
}
