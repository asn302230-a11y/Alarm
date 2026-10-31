package com.example.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.IraqAlarmApplication
import com.example.MainActivity
import com.example.R
import com.example.data.model.AlarmItem
import com.example.scheduler.AlarmScheduler
import com.example.util.SoundPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class AlarmRingingService : Service() {
    private val serviceScope = CoroutineScope(Dispatchers.IO + Job())
    private var ringingAlarm: AlarmItem? = null

    companion object {
        const val CHANNEL_ID = "iraq_alarm_channel"
        const val NOTIFICATION_ID = 1001

        const val ACTION_START_RINGING = "com.aistudio.iraqalarm.ACTION_START_RINGING"
        const val ACTION_STOP_RINGING = "com.aistudio.iraqalarm.ACTION_STOP_RINGING"

        fun start(context: Context, alarmId: Long) {
            val intent = Intent(context, AlarmRingingService::class.java).apply {
                action = ACTION_START_RINGING
                putExtra(AlarmScheduler.EXTRA_ALARM_ID, alarmId)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stop(context: Context) {
            val intent = Intent(context, AlarmRingingService::class.java).apply {
                action = ACTION_STOP_RINGING
            }
            context.stopService(intent)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP_RINGING) {
            stopAlarm()
            stopSelf()
            return START_NOT_STICKY
        }

        val alarmId = intent?.getLongExtra(AlarmScheduler.EXTRA_ALARM_ID, -1L) ?: -1L
        if (alarmId != -1L) {
            serviceScope.launch {
                val app = application as? IraqAlarmApplication
                val alarm = app?.repository?.getAlarmById(alarmId) ?: AlarmItem(
                    id = alarmId,
                    hour = 7,
                    minute = 0,
                    label = "Iraq Alarm"
                )
                ringingAlarm = alarm
                AlarmState.setRingingAlarm(alarm)

                val notification = buildNotification(alarm)
                startForeground(NOTIFICATION_ID, notification)

                SoundPlayer.playTone(this@AlarmRingingService, alarm.soundTone, loop = true)
                if (alarm.vibrate) {
                    SoundPlayer.startVibration(this@AlarmRingingService)
                }
            }
        }

        return START_STICKY
    }

    private fun buildNotification(alarm: AlarmItem): Notification {
        val fullScreenIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(AlarmScheduler.EXTRA_ALARM_ID, alarm.id)
            putExtra("EXTRA_IS_RINGING", true)
        }
        val fullScreenPendingIntent = PendingIntent.getActivity(
            this,
            alarm.id.toInt(),
            fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Dismiss action
        val dismissIntent = Intent(this, com.example.receiver.AlarmReceiver::class.java).apply {
            action = AlarmScheduler.ACTION_DISMISS_ALARM
            putExtra(AlarmScheduler.EXTRA_ALARM_ID, alarm.id)
        }
        val dismissPendingIntent = PendingIntent.getBroadcast(
            this,
            alarm.id.toInt() + 10000,
            dismissIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Snooze action
        val snoozeIntent = Intent(this, com.example.receiver.AlarmReceiver::class.java).apply {
            action = AlarmScheduler.ACTION_SNOOZE_ALARM
            putExtra(AlarmScheduler.EXTRA_ALARM_ID, alarm.id)
        }
        val snoozePendingIntent = PendingIntent.getBroadcast(
            this,
            alarm.id.toInt() + 20000,
            snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = "🇮🇶 ${alarm.label.ifBlank { "Iraq Alarm" }}"
        val content = "${alarm.formatted12HourTime} ${alarm.amPm} • الله أكبر"

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_iraq_alarm)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentIntent(fullScreenPendingIntent)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .setOngoing(true)
            .setAutoCancel(false)
            .addAction(0, "Snooze (${alarm.snoozeMinutes}m)", snoozePendingIntent)
            .addAction(0, "Stop / إيقاف", dismissPendingIntent)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Iraq Alarm Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "High priority alarms with Iraq flag theme"
                enableVibration(true)
                setSound(null, null) // Handled in-service by SoundPlayer
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    private fun stopAlarm() {
        SoundPlayer.stopTone()
        SoundPlayer.stopVibration()
        AlarmState.setRingingAlarm(null)
    }

    override fun onDestroy() {
        stopAlarm()
        super.onDestroy()
    }
}
