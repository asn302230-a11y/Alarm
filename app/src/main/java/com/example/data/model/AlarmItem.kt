package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Calendar

@Entity(tableName = "alarms")
data class AlarmItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val hour: Int,
    val minute: Int,
    val label: String = "Alarm",
    val isEnabled: Boolean = true,
    val daysOfWeek: List<Int> = emptyList(), // 1=Sun, 2=Mon, 3=Tue, 4=Wed, 5=Thu, 6=Fri, 7=Sat
    val vibrate: Boolean = true,
    val snoozeMinutes: Int = 5,
    val soundTone: String = "Mesopotamian Dawn",
    val challengeRequired: Boolean = false,
    val isSnoozed: Boolean = false,
    val snoozeTimestamp: Long = 0L
) {
    /**
     * Formatted time display string (e.g. "07:30")
     */
    val formattedTime: String
        get() = String.format("%02d:%02d", hour, minute)

    /**
     * Formatted 12-hour display string (e.g. "7:30")
     */
    val formatted12HourTime: String
        get() {
            val h = if (hour == 0) 12 else if (hour > 12) hour - 12 else hour
            return String.format("%d:%02d", h, minute)
        }

    val amPm: String
        get() = if (hour < 12) "AM" else "PM"

    /**
     * Checks if this alarm repeats on specified days
     */
    val isRepeating: Boolean
        get() = daysOfWeek.isNotEmpty()

    /**
     * Human readable repeat summary
     */
    val repeatSummary: String
        get() {
            if (daysOfWeek.isEmpty()) return "Once"
            if (daysOfWeek.size == 7) return "Every day"
            // In Iraq, standard weekend is Friday and Saturday
            val weekendDays = listOf(Calendar.FRIDAY, Calendar.SATURDAY)
            val weekdayDays = listOf(Calendar.SUNDAY, Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY)
            if (daysOfWeek.size == 2 && daysOfWeek.containsAll(weekendDays)) return "Weekends (Fri - Sat)"
            if (daysOfWeek.size == 5 && daysOfWeek.containsAll(weekdayDays)) return "Weekdays (Sun - Thu)"

            val dayNames = listOf(
                Calendar.SUNDAY to "Sun",
                Calendar.MONDAY to "Mon",
                Calendar.TUESDAY to "Tue",
                Calendar.WEDNESDAY to "Wed",
                Calendar.THURSDAY to "Thu",
                Calendar.FRIDAY to "Fri",
                Calendar.SATURDAY to "Sat"
            )
            return dayNames.filter { it.first in daysOfWeek }.joinToString(" ") { it.second }
        }

    /**
     * Calculate next trigger time in milliseconds from now
     */
    fun getNextTriggerMillis(currentTimeMillis: Long = System.currentTimeMillis()): Long {
        if (isSnoozed && snoozeTimestamp > currentTimeMillis) {
            return snoozeTimestamp
        }

        val now = Calendar.getInstance().apply { timeInMillis = currentTimeMillis }
        val target = Calendar.getInstance().apply {
            timeInMillis = currentTimeMillis
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if (daysOfWeek.isEmpty()) {
            // One-time alarm
            if (target.timeInMillis <= now.timeInMillis) {
                target.add(Calendar.DAY_OF_YEAR, 1)
            }
            return target.timeInMillis
        } else {
            // Repeating alarm: find next matching day
            var daysToAdd = 0
            while (daysToAdd < 7) {
                val checkDay = target.get(Calendar.DAY_OF_WEEK)
                val isToday = daysToAdd == 0
                val isTimePassed = target.timeInMillis <= now.timeInMillis

                if (daysOfWeek.contains(checkDay) && (!isToday || !isTimePassed)) {
                    return target.timeInMillis
                }
                target.add(Calendar.DAY_OF_YEAR, 1)
                daysToAdd++
            }
            return target.timeInMillis
        }
    }
}
