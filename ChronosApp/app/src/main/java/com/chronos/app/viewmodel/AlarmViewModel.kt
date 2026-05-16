package com.chronos.app.viewmodel

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chronos.app.receiver.AlarmReceiver
import com.chronos.app.ui.alarm.AlarmItem
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class AlarmViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _alarms = MutableStateFlow(
        listOf(
            AlarmItem(1, 7,  0,  "Morning Rise",  setOf(1,2,3,4,5), true),
            AlarmItem(2, 12, 30, "Lunch Break",   setOf(1,2,3,4,5), true),
            AlarmItem(3, 22, 0,  "Wind Down",     setOf(0,6),       false),
        )
    )
    val alarms: StateFlow<List<AlarmItem>> = _alarms

    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    fun addAlarm(hour: Int, minute: Int, label: String, days: Set<Int>) {
        val id = System.currentTimeMillis().toInt()
        val item = AlarmItem(id, hour, minute, label, days, true)
        _alarms.update { it + item }
        scheduleAlarm(item)
    }

    fun toggleAlarm(id: Int) {
        _alarms.update { list ->
            list.map { a ->
                if (a.id == id) {
                    val updated = a.copy(enabled = !a.enabled)
                    if (updated.enabled) scheduleAlarm(updated) else cancelAlarm(updated)
                    updated
                } else a
            }
        }
    }

    fun deleteAlarm(id: Int) {
        val alarm = _alarms.value.find { it.id == id } ?: return
        cancelAlarm(alarm)
        _alarms.update { it.filter { a -> a.id != id } }
    }

    fun toggleDay(id: Int, day: Int) {
        _alarms.update { list ->
            list.map { a ->
                if (a.id == id) {
                    val newDays = if (a.days.contains(day)) a.days - day else a.days + day
                    a.copy(days = newDays)
                } else a
            }
        }
    }

    private fun scheduleAlarm(alarm: AlarmItem) {
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, alarm.hour)
            set(Calendar.MINUTE,      alarm.minute)
            set(Calendar.SECOND,      0)
            set(Calendar.MILLISECOND, 0)
            if (before(Calendar.getInstance())) add(Calendar.DAY_OF_MONTH, 1)
        }

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = "com.chronos.app.ALARM_TRIGGER"
            putExtra("alarm_id",    alarm.id)
            putExtra("alarm_label", alarm.label)
        }
        val pi = PendingIntent.getBroadcast(
            context, alarm.id, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            alarmManager.setAlarmClock(
                AlarmManager.AlarmClockInfo(cal.timeInMillis, pi), pi
            )
        } catch (e: SecurityException) {
            // SCHEDULE_EXACT_ALARM not granted — fall back to inexact
            alarmManager.set(AlarmManager.RTC_WAKEUP, cal.timeInMillis, pi)
        }
    }

    private fun cancelAlarm(alarm: AlarmItem) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pi = PendingIntent.getBroadcast(
            context, alarm.id, intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        ) ?: return
        alarmManager.cancel(pi)
    }
}
