package com.example.scheduler

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.MainActivity
import com.example.data.model.AlarmItem
import com.example.receiver.AlarmReceiver

class AlarmScheduler(private val context: Context) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager

    companion object {
        private const val TAG = "AlarmScheduler"
        const val ACTION_TRIGGER_ALARM = "com.aistudio.iraqalarm.ACTION_TRIGGER_ALARM"
        const val ACTION_DISMISS_ALARM = "com.aistudio.iraqalarm.ACTION_DISMISS_ALARM"
        const val ACTION_SNOOZE_ALARM = "com.aistudio.iraqalarm.ACTION_SNOOZE_ALARM"
        const val EXTRA_ALARM_ID = "extra_alarm_id"
    }

    fun schedule(alarm: AlarmItem) {
        if (!alarm.isEnabled) return
        val targetMillis = alarm.getNextTriggerMillis()

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_TRIGGER_ALARM
            putExtra(EXTRA_ALARM_ID, alarm.id)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val showIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_ALARM_ID, alarm.id)
        }
        val showPendingIntent = PendingIntent.getActivity(
            context,
            alarm.id.toInt(),
            showIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (alarmManager != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (alarmManager.canScheduleExactAlarms()) {
                        val alarmClockInfo = AlarmManager.AlarmClockInfo(targetMillis, showPendingIntent)
                        alarmManager.setAlarmClock(alarmClockInfo, pendingIntent)
                    } else {
                        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, targetMillis, pendingIntent)
                    }
                } else {
                    val alarmClockInfo = AlarmManager.AlarmClockInfo(targetMillis, showPendingIntent)
                    alarmManager.setAlarmClock(alarmClockInfo, pendingIntent)
                }
                Log.d(TAG, "Scheduled alarm ID ${alarm.id} for $targetMillis")
            }
        } catch (e: SecurityException) {
            Log.e(TAG, "Cannot schedule exact alarm due to permission", e)
            try {
                alarmManager?.set(AlarmManager.RTC_WAKEUP, targetMillis, pendingIntent)
            } catch (ex: Exception) {
                Log.e(TAG, "Fallback alarm failed", ex)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to schedule alarm", e)
        }
    }

    fun cancel(alarmId: Long) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_TRIGGER_ALARM
            putExtra(EXTRA_ALARM_ID, alarmId)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmId.toInt(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null && alarmManager != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
            Log.d(TAG, "Cancelled alarm ID $alarmId")
        }
    }
}
