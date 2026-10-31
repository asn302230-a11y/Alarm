package com.example.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.IraqAlarmApplication
import com.example.scheduler.AlarmScheduler
import com.example.service.AlarmRingingService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlarmReceiver : BroadcastReceiver() {
    companion object {
        private const val TAG = "AlarmReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        val alarmId = intent.getLongExtra(AlarmScheduler.EXTRA_ALARM_ID, -1L)
        Log.d(TAG, "onReceive action=$action alarmId=$alarmId")

        val app = context.applicationContext as? IraqAlarmApplication ?: return

        when (action) {
            AlarmScheduler.ACTION_TRIGGER_ALARM -> {
                if (alarmId != -1L) {
                    AlarmRingingService.start(context, alarmId)
                }
            }
            AlarmScheduler.ACTION_DISMISS_ALARM -> {
                AlarmRingingService.stop(context)
                if (alarmId != -1L) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val alarm = app.repository.getAlarmById(alarmId) ?: return@launch
                        val updated = if (alarm.isRepeating) {
                            alarm.copy(isSnoozed = false, snoozeTimestamp = 0L)
                        } else {
                            alarm.copy(isEnabled = false, isSnoozed = false, snoozeTimestamp = 0L)
                        }
                        app.repository.updateAlarm(updated)
                        if (updated.isEnabled) {
                            app.alarmScheduler.schedule(updated)
                        }
                    }
                }
            }
            AlarmScheduler.ACTION_SNOOZE_ALARM -> {
                AlarmRingingService.stop(context)
                if (alarmId != -1L) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val alarm = app.repository.getAlarmById(alarmId) ?: return@launch
                        val snoozeMinutes = if (alarm.snoozeMinutes > 0) alarm.snoozeMinutes else 5
                        val snoozeUntil = System.currentTimeMillis() + (snoozeMinutes * 60 * 1000L)
                        val updated = alarm.copy(
                            isSnoozed = true,
                            snoozeTimestamp = snoozeUntil
                        )
                        app.repository.updateAlarm(updated)
                        app.alarmScheduler.schedule(updated)
                    }
                }
            }
        }
    }
}
