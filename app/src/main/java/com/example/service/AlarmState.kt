package com.example.service

import com.example.data.model.AlarmItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object AlarmState {
    private val _currentRingingAlarm = MutableStateFlow<AlarmItem?>(null)
    val currentRingingAlarm: StateFlow<AlarmItem?> = _currentRingingAlarm.asStateFlow()

    fun setRingingAlarm(alarm: AlarmItem?) {
        _currentRingingAlarm.value = alarm
    }
}
