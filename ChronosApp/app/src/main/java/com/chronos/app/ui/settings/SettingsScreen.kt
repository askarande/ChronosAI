package com.chronos.app.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.chronos.app.ui.theme.AppTheme
import com.chronos.app.viewmodel.SettingsViewModel

data class AppSettings(
    val theme: AppTheme = AppTheme.CYBER_TEAL,
    val use24h: Boolean = true,
    val showSeconds: Boolean = true,
    val showWeather: Boolean = true,
    val showBattery: Boolean = true,
    val font: ClockFont = ClockFont.ORBITRON,
    val language: String = "English",
    val flipSpeed: Int = 250
)

enum class ClockFont(val label: String) { ORBITRON("Orbitron"), SPACE_MONO("Space Mono"), RAJDHANI("Rajdhani") }

@Composable
fun SettingsScreen(vm: SettingsViewModel = hiltViewModel()) {
    val settings by vm.settings.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("Settings", style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground)
        }

        // Theme
        item {
            SettingsSection(title = "THEME") {
                ThemeGrid(current = settings.theme, onSelect = vm::setTheme)
            }
        }

        // Clock
        item {
            SettingsSection(title = "CLOCK") {
                SettingsToggleRow("24-Hour Format", Icons.Outlined.AccessTime, settings.use24h) { vm.toggle24h() }
                SettingsDivider()
                SettingsToggleRow("Show Seconds", Icons.Outlined.Timer, settings.showSeconds) { vm.toggleSeconds() }
                SettingsDivider()
                SettingsToggleRow("Show Weather", Icons.Outlined.WbSunny, settings.showWeather) { vm.toggleWeather() }
                SettingsDivider()
                SettingsToggleRow("Show Battery %", Icons.Outlined.BatteryFull, settings.showBattery) { vm.toggleBattery() }
            }
        }

        // Font
        item {
            SettingsSection(title = "CLOCK FONT") {
                ClockFont.values().forEach { font ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable { vm.setFont(font) }
                            .padding(vertical = 12.dp, horizontal = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(font.label, style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface)
                        RadioButton(
                            selected = settings.font == font,
                            onClick  = { vm.setFont(font) },
                            colors   = RadioButtonDefaults.colors(
                                selectedColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                    if (font != ClockFont.values().last()) SettingsDivider()
                }
            }
        }

        // Alarm defaults
        item {
            SettingsSection(title = "ALARM DEFAULTS") {
                SettingsInfoRow("Snooze Duration", Icons.Outlined.Snooze, "5 minutes")
                SettingsDivider()
                SettingsInfoRow("Default Ringtone", Icons.Outlined.MusicNote, "Digital Bell")
                SettingsDivider()
                SettingsInfoRow("Vibration", Icons.Outlined.Vibration, "Enabled")
            }
        }

        // AI Features
        item {
            SettingsSection(title = "AI FEATURES") {
                SettingsToggleRow("AI Productivity Tips", Icons.Outlined.AutoAwesome, true) {}
                SettingsDivider()
                SettingsToggleRow("Focus Score Tracking", Icons.Outlined.Insights, true) {}
                SettingsDivider()
                SettingsToggleRow("Smart Alarm (Gentle Wake)", Icons.Outlined.Alarm, false) {}
            }
        }

        // Language
        item {
            SettingsSection(title = "LANGUAGE") {
                listOf("English", "हिंदी", "मराठी", "தமிழ்", "తెలుగు", "বাংলা", "ગુજરાતી", "العربية", "日本語").forEach { lang ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable { vm.setLanguage(lang) }
                            .padding(vertical = 10.dp, horizontal = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(lang, style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface)
                        if (settings.language == lang) {
                            Icon(Icons.Outlined.Check, "Selected",
                                tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                        }
                    }
                    if (lang != "日本語") SettingsDivider()
                }
            }
        }

        // About
        item {
            SettingsSection(title = "ABOUT") {
                SettingsInfoRow("Version",      Icons.Outlined.Info,           "3.0.0 AI Edition")
                SettingsDivider()
                SettingsInfoRow("Ad-Free",      Icons.Outlined.Block,          "✓ Forever")
                SettingsDivider()
                SettingsInfoRow("No Permissions Required", Icons.Outlined.Security, "✓ Privacy first")
                SettingsDivider()
                SettingsInfoRow("Open Source",  Icons.Outlined.Code,           "GitHub →")
            }
        }
        item { Spacer(Modifier.height(32.dp)) }
    }
}

@Composable
fun ThemeGrid(current: AppTheme, onSelect: (AppTheme) -> Unit) {
    val themes = listOf(
        AppTheme.CYBER_TEAL to Triple("Cyber Teal", Color(0xFF00F5D4), Color(0xFF0A0A0F)),
        AppTheme.AURORA     to Triple("Aurora",     Color(0xFFA855F7), Color(0xFF0D0D1A)),
        AppTheme.EMBER      to Triple("Ember",      Color(0xFFF97316), Color(0xFF1A0A00)),
        AppTheme.ARCTIC     to Triple("Arctic",     Color(0xFF38BDF8), Color(0xFF0A0F1A)),
        AppTheme.MATRIX     to Triple("Matrix",     Color(0xFF00FF41), Color(0xFF000000)),
    )
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        themes.forEach { (theme, triple) ->
            val (name, accent, bg) = triple
            val selected = current == theme
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
                    .clickable { onSelect(theme) }
            ) {
                Box(
                    Modifier
                        .height(48.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(bg)
                        .border(
                            2.dp,
                            if (selected) accent else Color.Transparent,
                            RoundedCornerShape(10.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Box(Modifier.size(16.dp).clip(CircleShape).background(accent))
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    name,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (selected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column {
        Text(
            title,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape  = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp), content = content)
        }
    }
}

@Composable
fun SettingsToggleRow(
    label: String,
    icon: ImageVector,
    checked: Boolean,
    onToggle: () -> Unit
) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, label, modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(12.dp))
            Text(label, style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface)
        }
        Switch(
            checked = checked, onCheckedChange = { onToggle() },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.Black,
                checkedTrackColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}

@Composable
fun SettingsInfoRow(label: String, icon: ImageVector, value: String) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, label, modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(12.dp))
            Text(label, style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface)
        }
        Text(value, style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun SettingsDivider() = Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
