package com.chronos.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chronos.app.ui.music.FOCUS_TRACKS
import com.chronos.app.ui.music.MusicState
import com.chronos.app.ui.music.Track
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MusicViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(MusicState())
    val state: StateFlow<MusicState> = _state

    private var tickJob: Job? = null

    fun togglePlay() {
        val playing = !_state.value.playing
        _state.update { it.copy(playing = playing) }
        if (playing) startTick() else tickJob?.cancel()
    }

    fun playTrack(track: Track) {
        tickJob?.cancel()
        _state.update { it.copy(currentTrack = track, positionSec = 0, playing = true) }
        startTick()
    }

    fun next() {
        val idx = (FOCUS_TRACKS.indexOf(_state.value.currentTrack) + 1) % FOCUS_TRACKS.size
        playTrack(FOCUS_TRACKS[idx])
    }

    fun prev() {
        val idx = (FOCUS_TRACKS.indexOf(_state.value.currentTrack) - 1 + FOCUS_TRACKS.size) % FOCUS_TRACKS.size
        playTrack(FOCUS_TRACKS[idx])
    }

    fun toggleShuffle() = _state.update { it.copy(shuffle = !it.shuffle) }
    fun toggleRepeat()  = _state.update { it.copy(repeat  = !it.repeat) }

    private fun startTick() {
        tickJob?.cancel()
        tickJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                val s = _state.value
                val next = s.positionSec + 1
                if (next >= s.currentTrack.durationSec) {
                    if (s.repeat) _state.update { it.copy(positionSec = 0) }
                    else next()
                } else {
                    _state.update { it.copy(positionSec = next) }
                }
            }
        }
    }
}
