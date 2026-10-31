package com.example.data.repository

import com.example.data.local.AlarmDao
import com.example.data.model.AlarmItem
import kotlinx.coroutines.flow.Flow

class AlarmRepository(private val alarmDao: AlarmDao) {
    val allAlarms: Flow<List<AlarmItem>> = alarmDao.getAllAlarms()

    suspend fun getEnabledAlarms(): List<AlarmItem> = alarmDao.getEnabledAlarms()

    suspend fun getAlarmById(id: Long): AlarmItem? = alarmDao.getAlarmById(id)

    suspend fun insertAlarm(alarm: AlarmItem): Long = alarmDao.insertAlarm(alarm)

    suspend fun updateAlarm(alarm: AlarmItem) = alarmDao.updateAlarm(alarm)

    suspend fun deleteAlarm(alarm: AlarmItem) = alarmDao.deleteAlarm(alarm)

    suspend fun deleteAlarmById(id: Long) = alarmDao.deleteAlarmById(id)

    suspend fun setAlarmEnabled(id: Long, isEnabled: Boolean) = alarmDao.setAlarmEnabled(id, isEnabled)
}
