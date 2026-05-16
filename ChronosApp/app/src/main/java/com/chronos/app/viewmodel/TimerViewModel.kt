package com.chronos.app.viewmodel

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chronos.app.ChronosApplication.Companion.CHANNEL_TIMER
import com.chronos.app.ui.timer.TimerState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TimerViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(TimerState())
    val state: StateFlow<TimerState> = _state

    private var tickJob: Job? = null
    private var deadlineMs = 0L

    fun setH(v: Int) = _state.update { it.copy(inputH = v) }
    fun setM(v: Int) = _state.update { it.copy(inputM = v) }
    fun setS(v: Int) = _state.update { it.copy(inputS = v) }

    fun setPreset(seconds: Int) {
        val h = seconds / 3600
        val m = (seconds % 3600) / 60
        val s = seconds % 60
        _state.update { it.copy(inputH = h, inputM = m, inputS = s) }
    }

    fun start() {
        val s = _state.value
        val total = s.inputH * 3600 + s.inputM * 60 + s.inputS
        if (total <= 0) return
        deadlineMs = System.currentTimeMillis() + total * 1000L
        _state.update {
            it.copy(active = true, running = true, totalSeconds = total, remainingMs = total * 1000L)
        }
        tick()
    }

    fun pause() {
        tickJob?.cancel()
        _state.update { it.copy(running = false) }
    }

    fun resume() {
        val rem = _state.value.remainingMs
        deadlineMs = System.currentTimeMillis() + rem
        _state.update { it.copy(running = true) }
        tick()
    }

    fun reset() {
        tickJob?.cancel()
        _state.update { it.copy(active = false, running = false, remainingMs = 0, totalSeconds = 0) }
    }

    private fun tick() {
        tickJob?.cancel()
        tickJob = viewModelScope.launch {
            while (true) {
                delay(100)
                val rem = deadlineMs - System.currentTimeMillis()
                if (rem <= 0) {
                    _state.update { it.copy(running = false, remainingMs = 0) }
                    fireTimerDoneNotification()
                    break
                }
                _state.update { it.copy(remainingMs = rem) }
            }
        }
    }

    private fun fireTimerDoneNotification() {
        val nm = context.getSystemService(NotificationManager::class.java)
        val notif = NotificationCompat.Builder(context, CHANNEL_TIMER)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("⏰ Timer Done!")
            .setContentText("Your Chronos timer has finished.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
        nm.notify(2001, notif)
    }
}
