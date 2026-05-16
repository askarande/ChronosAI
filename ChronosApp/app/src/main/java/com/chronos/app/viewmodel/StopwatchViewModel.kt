package com.chronos.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chronos.app.ui.stopwatch.LapEntry
import com.chronos.app.ui.stopwatch.StopwatchState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StopwatchViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(StopwatchState())
    val state: StateFlow<StopwatchState> = _state

    private var tickJob: Job? = null
    private var startTime = 0L
    private var accumulated = 0L
    private var lastLapTotal = 0L

    fun start() {
        if (_state.value.running) return
        startTime = System.currentTimeMillis()
        _state.update { it.copy(running = true) }
        tickJob = viewModelScope.launch {
            while (true) {
                delay(10)
                _state.update {
                    it.copy(elapsed = accumulated + (System.currentTimeMillis() - startTime))
                }
            }
        }
    }

    fun stop() {
        accumulated += System.currentTimeMillis() - startTime
        tickJob?.cancel()
        _state.update { it.copy(running = false) }
    }

    fun reset() {
        tickJob?.cancel()
        accumulated = 0L
        lastLapTotal = 0L
        _state.value = StopwatchState()
    }

    fun lap() {
        val total = _state.value.elapsed
        val split = total - lastLapTotal
        lastLapTotal = total
        val newLap = LapEntry(
            index   = _state.value.laps.size + 1,
            totalMs = total,
            splitMs = split
        )
        _state.update { it.copy(laps = it.laps + newLap) }
    }
}
