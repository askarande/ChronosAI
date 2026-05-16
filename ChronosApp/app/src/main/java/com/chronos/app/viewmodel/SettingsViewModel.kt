package com.chronos.app.viewmodel

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chronos.app.ui.settings.AppSettings
import com.chronos.app.ui.settings.ClockFont
import com.chronos.app.ui.theme.AppTheme
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "chronos_settings")

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val THEME_KEY    = stringPreferencesKey("theme")
    private val USE24H_KEY   = booleanPreferencesKey("use24h")
    private val SECONDS_KEY  = booleanPreferencesKey("showSeconds")
    private val WEATHER_KEY  = booleanPreferencesKey("showWeather")
    private val BATTERY_KEY  = booleanPreferencesKey("showBattery")
    private val FONT_KEY     = stringPreferencesKey("font")
    private val LANG_KEY     = stringPreferencesKey("language")

    private val _settings = MutableStateFlow(AppSettings())
    val settings: StateFlow<AppSettings> = _settings

    init {
        viewModelScope.launch {
            context.dataStore.data.map { prefs ->
                AppSettings(
                    theme        = AppTheme.valueOf(prefs[THEME_KEY] ?: AppTheme.CYBER_TEAL.name),
                    use24h       = prefs[USE24H_KEY]  ?: true,
                    showSeconds  = prefs[SECONDS_KEY] ?: true,
                    showWeather  = prefs[WEATHER_KEY] ?: true,
                    showBattery  = prefs[BATTERY_KEY] ?: true,
                    font         = ClockFont.valueOf(prefs[FONT_KEY] ?: ClockFont.ORBITRON.name),
                    language     = prefs[LANG_KEY] ?: "English"
                )
            }.collect { _settings.value = it }
        }
    }

    fun setTheme(theme: AppTheme) = save { it[THEME_KEY] = theme.name }
    fun toggle24h()       = save { it[USE24H_KEY]  = !(_settings.value.use24h) }
    fun toggleSeconds()   = save { it[SECONDS_KEY] = !(_settings.value.showSeconds) }
    fun toggleWeather()   = save { it[WEATHER_KEY] = !(_settings.value.showWeather) }
    fun toggleBattery()   = save { it[BATTERY_KEY] = !(_settings.value.showBattery) }
    fun setFont(f: ClockFont) = save { it[FONT_KEY] = f.name }
    fun setLanguage(l: String) = save { it[LANG_KEY] = l }

    private fun save(block: (MutablePreferences) -> Unit) {
        viewModelScope.launch {
            context.dataStore.edit(block)
        }
    }
}
