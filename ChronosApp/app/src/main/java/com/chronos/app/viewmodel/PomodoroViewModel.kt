package com.chronos.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chronos.app.ui.pomodoro.PomodoroMode
import com.chronos.app.ui.pomodoro.PomodoroState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PomodoroViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(PomodoroState())
    val state: StateFlow<PomodoroState> = _state

    private var tickJob: Job? = null
    private var deadline = 0L

    fun setMode(mode: PomodoroMode) {
        tickJob?.cancel()
        _state.update {
            it.copy(
                mode          = mode,
                running       = false,
                totalSeconds  = mode.seconds,
                remainingMs   = mode.seconds * 1000L
            )
        }
    }

    fun start() {
        val s = _state.value
        if (s.running) return
        deadline = System.currentTimeMillis() + s.remainingMs
        _state.update { it.copy(running = true) }
        tickJob = viewModelScope.launch {
            while (true) {
                delay(100)
                val rem = deadline - System.currentTimeMillis()
                if (rem <= 0) {
                    onSessionComplete()
                    break
                }
                _state.update { it.copy(remainingMs = rem) }
            }
        }
    }

    fun pause() {
        tickJob?.cancel()
        _state.update { it.copy(running = false) }
    }

    fun reset() {
        tickJob?.cancel()
        val mode = _state.value.mode
        _state.update {
            it.copy(
                running      = false,
                totalSeconds = mode.seconds,
                remainingMs  = mode.seconds * 1000L
            )
        }
    }

    private fun onSessionComplete() {
        val s = _state.value
        val newCompleted = s.completedSessions + 1
        // After 4 focus sessions, suggest long break
        val nextMode = when {
            s.mode == PomodoroMode.FOCUS && newCompleted % 4 == 0 -> PomodoroMode.LONG
            s.mode == PomodoroMode.FOCUS -> PomodoroMode.SHORT
            else -> PomodoroMode.FOCUS
        }
        _state.update {
            it.copy(
                running           = false,
                completedSessions = if (s.mode == PomodoroMode.FOCUS) newCompleted else s.completedSessions,
                mode              = nextMode,
                totalSeconds      = nextMode.seconds,
                remainingMs       = nextMode.seconds * 1000L
            )
        }
    }
}
