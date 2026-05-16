package com.chronos.app.viewmodel

import androidx.lifecycle.ViewModel
import com.chronos.app.ui.worldclock.DEFAULT_CITIES
import com.chronos.app.ui.worldclock.WorldCity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class WorldClockViewModel @Inject constructor() : ViewModel() {

    private val _cities = MutableStateFlow(DEFAULT_CITIES)
    val cities: StateFlow<List<WorldCity>> = _cities

    fun showSearch() { /* Open city search bottom sheet */ }

    fun addCity(city: WorldCity) {
        if (_cities.value.none { it.city == city.city }) {
            _cities.value = _cities.value + city
        }
    }

    fun removeCity(cityName: String) {
        _cities.value = _cities.value.filter { it.city != cityName }
    }
}
