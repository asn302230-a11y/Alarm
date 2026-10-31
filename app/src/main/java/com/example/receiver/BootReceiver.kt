package com.example.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.IraqAlarmApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    companion object {
        private const val TAG = "BootReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        Log.d(TAG, "Boot/Time change event received: $action")

        val app = context.applicationContext as? IraqAlarmApplication ?: return
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val enabledAlarms = app.repository.getEnabledAlarms()
                Log.d(TAG, "Rescheduling ${enabledAlarms.size} alarms after $action")
                for (alarm in enabledAlarms) {
                    app.alarmScheduler.schedule(alarm)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to reschedule alarms on reboot", e)
            }
        }
    }
}
