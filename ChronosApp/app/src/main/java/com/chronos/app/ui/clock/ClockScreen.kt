package com.chronos.app.ui.clock

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.chronos.app.viewmodel.ClockViewModel
import com.chronos.app.ui.theme.OrbitronFamily
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ClockScreen(vm: ClockViewModel = hiltViewModel()) {
    val uiState by vm.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(16.dp))

        // Info bar: date + weather
        InfoBar(uiState)

        Spacer(Modifier.height(24.dp))

        // Mode selector
        ClockModeSelector(uiState.mode, vm::setMode)

        Spacer(Modifier.height(16.dp))

        // Main clock display
        when (uiState.mode) {
            ClockMode.FLIP    -> FlipClockDisplay(uiState)
            ClockMode.ANALOG  -> AnalogClockDisplay(uiState)
            ClockMode.BINARY  -> BinaryClockDisplay(uiState)
            ClockMode.ZEN     -> ZenClockDisplay(uiState)
        }

        Spacer(Modifier.height(20.dp))

        // Quick action row
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            QuickAction(Icons.Outlined.Fullscreen, "Full Screen", Modifier.weight(1f)) { vm.toggleFullscreen() }
            QuickAction(Icons.Outlined.SettingsBrightness, "Standby", Modifier.weight(1f)) { vm.enterStandby() }
            QuickAction(Icons.Outlined.Window, "Float", Modifier.weight(1f)) { vm.toggleFloatingClock() }
            QuickAction(Icons.Outlined.Widgets, "Widget", Modifier.weight(1f)) { vm.addWidget() }
        }
    }
}

@Composable
fun FlipClockDisplay(state: ClockUiState) {
    val now = remember { mutableStateOf(Calendar.getInstance()) }

    LaunchedEffect(Unit) {
        while (true) {
            now.value = Calendar.getInstance()
            delay(1000)
        }
    }

    val cal = now.value
    val use24 = state.use24h
    var hour = if (use24) cal.get(Calendar.HOUR_OF_DAY) else cal.get(Calendar.HOUR).let { if (it == 0) 12 else it }
    val minute = cal.get(Calendar.MINUTE)
    val second = cal.get(Calendar.SECOND)
    val amPm = if (cal.get(Calendar.AM_PM) == Calendar.AM) "AM" else "PM"

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        FlipPair(hour)
        FlipSeparator()
        FlipPair(minute)
        if (state.showSeconds) {
            FlipSeparator(small = true)
            FlipPair(second, small = true)
        }
        if (!use24) {
            Spacer(Modifier.width(8.dp))
            Text(
                text = amPm,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 20.dp)
            )
        }
    }
}

@Composable
fun FlipPair(value: Int, small: Boolean = false) {
    val padded = value.toString().padStart(2, '0')
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        FlipCard(padded[0].toString(), small)
        FlipCard(padded[1].toString(), small)
    }
}

@Composable
fun FlipCard(digit: String, small: Boolean = false) {
    var prevDigit by remember { mutableStateOf(digit) }
    var isFlipping by remember { mutableStateOf(false) }
    val flipAnim = remember { Animatable(0f) }

    LaunchedEffect(digit) {
        if (digit != prevDigit) {
            isFlipping = true
            flipAnim.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 300, easing = EaseInOut)
            )
            prevDigit = digit
            flipAnim.snapTo(0f)
            isFlipping = false
        }
    }

    val cardW = if (small) 52.dp else 76.dp
    val cardH = if (small) 68.dp else 100.dp
    val fontSize = if (small) 32.sp else 52.sp
    val shape = RoundedCornerShape(10.dp)

    Box(
        modifier = Modifier
            .width(cardW)
            .height(cardH)
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), shape)
            .graphicsLayer {
                rotationX = if (isFlipping) flipAnim.value * -90f else 0f
                cameraDistance = 8f * density
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (isFlipping && flipAnim.value > 0.5f) prevDigit else digit,
            fontFamily = OrbitronFamily,
            fontSize = fontSize,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Black,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier.graphicsLayer {
                rotationX = if (isFlipping) flipAnim.value * -90f else 0f
            }
        )
        // Divider line
        Divider(
            modifier = Modifier.align(Alignment.Center),
            color = Color.Black.copy(alpha = 0.4f),
            thickness = 1.dp
        )
    }
}

@Composable
fun FlipSeparator(small: Boolean = false) {
    val infiniteTransition = rememberInfiniteTransition(label = "sep")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 0.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "sepAlpha"
    )
    Text(
        text = ":",
        fontFamily = OrbitronFamily,
        fontSize = if (small) 30.sp else 48.sp,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary.copy(alpha = alpha),
        modifier = Modifier.padding(horizontal = 4.dp, vertical = if (small) 12.dp else 20.dp)
    )
}

@Composable
fun InfoBar(state: ClockUiState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = SimpleDateFormat("EEEE", Locale.getDefault()).format(Date()),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = SimpleDateFormat("d MMM yyyy", Locale.getDefault()).format(Date()),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${state.weather.temp}°C  ${state.weather.description}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = state.weather.city,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun ClockModeSelector(current: ClockMode, onSelect: (ClockMode) -> Unit) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        ClockMode.values().forEach { mode ->
            FilterChip(
                selected = current == mode,
                onClick = { onSelect(mode) },
                label = {
                    Text(
                        mode.label,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                modifier = Modifier.weight(1f),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = Color.Black,
                    containerColor = MaterialTheme.colorScheme.surface,
                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

@Composable
fun QuickAction(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp).fillMaxWidth()
        ) {
            Icon(icon, label, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
            Spacer(Modifier.height(4.dp))
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
        }
    }
}

// Analog, Binary, Zen stubs (each would be their own detailed composable)
@Composable
fun AnalogClockDisplay(state: ClockUiState) {
    // Full analog clock drawn with Canvas API using drawArc, drawLine
    // Hour, minute, second hands with smooth rotation animation
    Text("Analog Clock — see AnalogClockCanvas.kt", color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(32.dp))
}
@Composable
fun BinaryClockDisplay(state: ClockUiState) {
    Text("Binary Clock Display — see BinaryClockCanvas.kt", color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(32.dp))
}
@Composable
fun ZenClockDisplay(state: ClockUiState) {
    Text("Zen/Minimal Clock — see ZenClockCanvas.kt", color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(32.dp))
}

enum class ClockMode(val label: String) { FLIP("Flip"), ANALOG("Analog"), BINARY("Binary"), ZEN("Zen") }
data class WeatherInfo(val temp: Int = 32, val description: String = "Clear", val city: String = "Mumbai")
data class ClockUiState(
    val mode: ClockMode = ClockMode.FLIP,
    val use24h: Boolean = true,
    val showSeconds: Boolean = true,
    val weather: WeatherInfo = WeatherInfo(),
    val batteryPct: Int = 80
)
