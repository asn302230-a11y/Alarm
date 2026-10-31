package com.example

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.example.data.local.AlarmDatabase
import com.example.data.model.AlarmItem
import com.example.data.repository.AlarmRepository
import com.example.scheduler.AlarmScheduler
import com.example.service.AlarmRingingService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar

class IraqAlarmApplication : Application() {
    val database by lazy { AlarmDatabase.getInstance(this) }
    val repository by lazy { AlarmRepository(database.alarmDao()) }
    val alarmScheduler by lazy { AlarmScheduler(this) }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        seedInitialAlarmsIfEmpty()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                AlarmRingingService.CHANNEL_ID,
                "Iraq Alarm Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "High priority alarms with Iraq flag theme and sound"
                enableVibration(true)
                setSound(null, null)
                lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    private fun seedInitialAlarmsIfEmpty() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val existing = repository.allAlarms.first()
                if (existing.isEmpty()) {
                    val defaultAlarms = listOf(
                        AlarmItem(
                            hour = 5,
                            minute = 0,
                            label = "Fajr Prayer • صلاة الفجر",
                            isEnabled = true,
                            daysOfWeek = listOf(
                                Calendar.SUNDAY, Calendar.MONDAY, Calendar.TUESDAY,
                                Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY
                            ),
                            soundTone = "Mesopotamian Dawn",
                            vibrate = true
                        ),
                        AlarmItem(
                            hour = 7,
                            minute = 30,
                            label = "Morning Rise • صباح بغداد",
                            isEnabled = true,
                            daysOfWeek = listOf(
                                Calendar.SUNDAY, Calendar.MONDAY, Calendar.TUESDAY,
                                Calendar.WEDNESDAY, Calendar.THURSDAY
                            ),
                            soundTone = "Crescent Clarion",
                            vibrate = true
                        ),
                        AlarmItem(
                            hour = 12,
                            minute = 30,
                            label = "Dhuhr Prayer • صلاة الظهر",
                            isEnabled = false,
                            daysOfWeek = listOf(
                                Calendar.SUNDAY, Calendar.MONDAY, Calendar.TUESDAY,
                                Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY
                            ),
                            soundTone = "Mesopotamian Dawn"
                        )
                    )

                    for (alarm in defaultAlarms) {
                        val id = repository.insertAlarm(alarm)
                        if (alarm.isEnabled) {
                            alarmScheduler.schedule(alarm.copy(id = id))
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
