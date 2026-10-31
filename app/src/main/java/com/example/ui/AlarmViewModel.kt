package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.IraqAlarmApplication
import com.example.data.model.AlarmItem
import com.example.scheduler.AlarmScheduler
import com.example.service.AlarmRingingService
import com.example.service.AlarmState
import com.example.util.SoundPlayer
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class AlarmViewModel(application: Application) : AndroidViewModel(application) {
    private val app = application as IraqAlarmApplication
    private val repository = app.repository
    private val alarmScheduler = app.alarmScheduler

    val alarms: StateFlow<List<AlarmItem>> = repository.allAlarms
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val ringingAlarm: StateFlow<AlarmItem?> = AlarmState.currentRingingAlarm

    private val _selectedTab = MutableStateFlow(0) // 0: Alarms, 1: Baghdad Clock, 2: Timer/Stopwatch, 3: Sleep
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    private val _editingAlarm = MutableStateFlow<AlarmItem?>(null)
    val editingAlarm: StateFlow<AlarmItem?> = _editingAlarm.asStateFlow()

    private val _isCreatingNew = MutableStateFlow(false)
    val isCreatingNew: StateFlow<Boolean> = _isCreatingNew.asStateFlow()

    // Clock tickers
    private val _currentTimeMillis = MutableStateFlow(System.currentTimeMillis())
    val currentTimeMillis: StateFlow<Long> = _currentTimeMillis.asStateFlow()

    // Preview sound state
    private val _previewingTone = MutableStateFlow<String?>(null)
    val previewingTone: StateFlow<String?> = _previewingTone.asStateFlow()

    private var clockJob: Job? = null

    init {
        startClock()
    }

    private fun startClock() {
        clockJob?.cancel()
        clockJob = viewModelScope.launch {
            while (isActive) {
                _currentTimeMillis.value = System.currentTimeMillis()
                delay(1000)
            }
        }
    }

    fun setSelectedTab(tab: Int) {
        _selectedTab.value = tab
    }

    fun startCreateAlarm() {
        val cal = Calendar.getInstance()
        cal.add(Calendar.HOUR_OF_DAY, 1)
        _editingAlarm.value = AlarmItem(
            hour = cal.get(Calendar.HOUR_OF_DAY),
            minute = 0,
            label = "Alarm",
            isEnabled = true,
            daysOfWeek = listOf(
                Calendar.SUNDAY, Calendar.MONDAY, Calendar.TUESDAY,
                Calendar.WEDNESDAY, Calendar.THURSDAY
            ),
            soundTone = "Mesopotamian Dawn",
            vibrate = true,
            snoozeMinutes = 5
        )
        _isCreatingNew.value = true
    }

    fun startEditAlarm(alarm: AlarmItem) {
        _editingAlarm.value = alarm
        _isCreatingNew.value = false
    }

    fun cancelEditAlarm() {
        _editingAlarm.value = null
        _isCreatingNew.value = false
        stopPreviewTone()
    }

    fun saveAlarm(alarm: AlarmItem) {
        viewModelScope.launch {
            stopPreviewTone()
            if (_isCreatingNew.value || alarm.id == 0L) {
                val newId = repository.insertAlarm(alarm)
                if (alarm.isEnabled) {
                    alarmScheduler.schedule(alarm.copy(id = newId))
                }
            } else {
                repository.updateAlarm(alarm)
                if (alarm.isEnabled) {
                    alarmScheduler.schedule(alarm)
                } else {
                    alarmScheduler.cancel(alarm.id)
                }
            }
            _editingAlarm.value = null
            _isCreatingNew.value = false
        }
    }

    fun toggleAlarm(alarm: AlarmItem) {
        viewModelScope.launch {
            val updated = alarm.copy(isEnabled = !alarm.isEnabled, isSnoozed = false, snoozeTimestamp = 0L)
            repository.updateAlarm(updated)
            if (updated.isEnabled) {
                alarmScheduler.schedule(updated)
            } else {
                alarmScheduler.cancel(updated.id)
            }
        }
    }

    fun deleteAlarm(alarm: AlarmItem) {
        viewModelScope.launch {
            alarmScheduler.cancel(alarm.id)
            repository.deleteAlarm(alarm)
            if (_editingAlarm.value?.id == alarm.id) {
                _editingAlarm.value = null
                _isCreatingNew.value = false
            }
        }
    }

    fun previewTone(toneName: String) {
        if (_previewingTone.value == toneName) {
            stopPreviewTone()
        } else {
            _previewingTone.value = toneName
            SoundPlayer.playTone(getApplication(), toneName, loop = false)
        }
    }

    fun stopPreviewTone() {
        _previewingTone.value = null
        SoundPlayer.stopTone()
    }

    fun dismissRinging() {
        AlarmRingingService.stop(getApplication())
        val current = ringingAlarm.value
        if (current != null) {
            viewModelScope.launch {
                val updated = if (current.isRepeating) {
                    current.copy(isSnoozed = false, snoozeTimestamp = 0L)
                } else {
                    current.copy(isEnabled = false, isSnoozed = false, snoozeTimestamp = 0L)
                }
                repository.updateAlarm(updated)
                if (updated.isEnabled) {
                    alarmScheduler.schedule(updated)
                }
            }
        }
        AlarmState.setRingingAlarm(null)
    }

    fun snoozeRinging(minutes: Int? = null) {
        AlarmRingingService.stop(getApplication())
        val current = ringingAlarm.value
        if (current != null) {
            val snoozeMin = minutes ?: if (current.snoozeMinutes > 0) current.snoozeMinutes else 5
            val snoozeUntil = System.currentTimeMillis() + (snoozeMin * 60 * 1000L)
            viewModelScope.launch {
                val updated = current.copy(isSnoozed = true, snoozeTimestamp = snoozeUntil)
                repository.updateAlarm(updated)
                alarmScheduler.schedule(updated)
            }
        }
        AlarmState.setRingingAlarm(null)
    }

    fun triggerTestAlarm(alarm: AlarmItem) {
        AlarmRingingService.start(getApplication(), alarm.id)
    }

    /**
     * Calculates time remaining until next enabled alarm
     */
    fun getNextAlarmTimeRemaining(): String? {
        val enabledList = alarms.value.filter { it.isEnabled }
        if (enabledList.isEmpty()) return null

        val now = System.currentTimeMillis()
        val nextTrigger = enabledList.map { it.getNextTriggerMillis(now) }.minOrNull() ?: return null
        val diffMs = nextTrigger - now
        if (diffMs <= 0) return "Ringing soon"

        val hours = (diffMs / (1000 * 60 * 60)).toInt()
        val minutes = ((diffMs % (1000 * 60 * 60)) / (1000 * 60)).toInt()

        return when {
            hours > 0 && minutes > 0 -> "Alarm in $hours hr $minutes min"
            hours > 0 -> "Alarm in $hours hr"
            minutes > 0 -> "Alarm in $minutes min"
            else -> "Alarm in less than 1 min"
        }
    }

    /**
     * Format Baghdad time (GMT+3)
     */
    fun getBaghdadTime(): String {
        val sdf = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("Asia/Baghdad")
        return sdf.format(Date(currentTimeMillis.value))
    }

    fun getBaghdadDate(): String {
        val sdf = SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("Asia/Baghdad")
        return sdf.format(Date(currentTimeMillis.value))
    }

    override fun onCleared() {
        clockJob?.cancel()
        stopPreviewTone()
        super.onCleared()
    }
}
