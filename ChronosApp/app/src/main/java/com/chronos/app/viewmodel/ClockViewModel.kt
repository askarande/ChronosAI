package com.chronos.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chronos.app.ui.clock.ClockMode
import com.chronos.app.ui.clock.ClockUiState
import com.chronos.app.ui.clock.WeatherInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClockViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(ClockUiState())
    val uiState: StateFlow<ClockUiState> = _uiState

    init {
        fetchWeather()
    }

    fun setMode(mode: ClockMode) = _uiState.update { it.copy(mode = mode) }

    fun toggleFullscreen() { /* send event to Activity */ }
    fun enterStandby()     { /* dim screen, show clock fullscreen */ }
    fun toggleFloatingClock() { /* start/stop FloatingClockService */ }
    fun addWidget()        { /* guide user to home screen widget */ }

    private fun fetchWeather() {
        viewModelScope.launch {
            // In production: call WeatherRepository -> OpenWeatherMap API
            delay(500)
            _uiState.update {
                it.copy(weather = WeatherInfo(32, "☀️ Clear", "Mumbai"))
            }
        }
    }
}
