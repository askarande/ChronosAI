package com.chronos.app.ui.timer

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.chronos.app.ui.theme.OrbitronFamily
import com.chronos.app.viewmodel.TimerViewModel

data class TimerPreset(val label: String, val seconds: Int)

val PRESETS = listOf(
    TimerPreset("1 min",  60),
    TimerPreset("5 min",  300),
    TimerPreset("10 min", 600),
    TimerPreset("15 min", 900),
    TimerPreset("25 min", 1500),
    TimerPreset("30 min", 1800),
    TimerPreset("45 min", 2700),
    TimerPreset("1 hr",   3600),
)

@Composable
fun TimerScreen(vm: TimerViewModel = hiltViewModel()) {
    val state by vm.state.collectAsState()
    val primary = MaterialTheme.colorScheme.primary
    val error   = MaterialTheme.colorScheme.error
    val surface = MaterialTheme.colorScheme.surface

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(16.dp))

        if (!state.active) {
            // Input row
            TimeInputRow(
                hours   = state.inputH,
                minutes = state.inputM,
                seconds = state.inputS,
                onH = vm::setH, onM = vm::setM, onS = vm::setS
            )
            Spacer(Modifier.height(24.dp))

            // Preset chips
            Text(
                "QUICK SET",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(Modifier.height(8.dp))
            androidx.compose.foundation.lazy.LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(PRESETS.size) { i ->
                    val p = PRESETS[i]
                    FilterChip(
                        selected = false,
                        onClick  = { vm.setPreset(p.seconds) },
                        label    = { Text(p.label, style = MaterialTheme.typography.labelSmall) },
                        colors   = FilterChipDefaults.filterChipColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            labelColor     = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
            }
        } else {
            // Ring + time display
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(240.dp)) {
                Canvas(Modifier.fillMaxSize()) {
                    val stroke = 18f
                    val inset  = stroke / 2
                    drawArc(
                        color      = surface,
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter  = false,
                        topLeft    = Offset(inset, inset),
                        size       = Size(size.width - stroke, size.height - stroke),
                        style      = Stroke(stroke, cap = StrokeCap.Round)
                    )
                    val progress = if (state.totalSeconds > 0)
                        state.remainingMs.toFloat() / (state.totalSeconds * 1000f) else 1f
                    drawArc(
                        color      = if (state.remainingMs < 60_000) error else primary,
                        startAngle = -90f,
                        sweepAngle = 360f * progress,
                        useCenter  = false,
                        topLeft    = Offset(inset, inset),
                        size       = Size(size.width - stroke, size.height - stroke),
                        style      = Stroke(stroke, cap = StrokeCap.Round)
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val rem = state.remainingMs / 1000
                    val h   = rem / 3600
                    val m   = (rem % 3600) / 60
                    val s   = rem % 60
                    val display = if (h > 0) "%d:%02d:%02d".format(h, m, s) else "%02d:%02d".format(m, s)
                    Text(
                        text       = display,
                        fontFamily = OrbitronFamily,
                        fontSize   = 44.sp,
                        fontWeight = FontWeight.Black,
                        color      = if (state.remainingMs < 60_000) error else MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text  = if (state.running) "RUNNING" else "PAUSED",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (state.running) primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(Modifier.height(32.dp))

        // Controls
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            when {
                !state.active -> {
                    Button(
                        onClick = vm::start,
                        colors  = ButtonDefaults.buttonColors(containerColor = primary),
                        shape   = RoundedCornerShape(40.dp),
                        modifier = Modifier.width(160.dp).height(52.dp)
                    ) {
                        Icon(Icons.Outlined.PlayArrow, "Start")
                        Spacer(Modifier.width(8.dp))
                        Text("START", fontWeight = FontWeight.Bold)
                    }
                }
                state.running -> {
                    OutlinedButton(
                        onClick = vm::pause,
                        shape   = RoundedCornerShape(40.dp),
                        modifier = Modifier.width(130.dp).height(52.dp)
                    ) {
                        Icon(Icons.Outlined.Pause, "Pause")
                        Spacer(Modifier.width(8.dp))
                        Text("PAUSE")
                    }
                    Button(
                        onClick = vm::reset,
                        colors  = ButtonDefaults.buttonColors(containerColor = error),
                        shape   = RoundedCornerShape(40.dp),
                        modifier = Modifier.width(130.dp).height(52.dp)
                    ) {
                        Icon(Icons.Outlined.Stop, "Reset")
                        Spacer(Modifier.width(8.dp))
                        Text("RESET")
                    }
                }
                else -> {
                    Button(
                        onClick = vm::resume,
                        colors  = ButtonDefaults.buttonColors(containerColor = primary),
                        shape   = RoundedCornerShape(40.dp),
                        modifier = Modifier.width(130.dp).height(52.dp)
                    ) {
                        Icon(Icons.Outlined.PlayArrow, "Resume")
                        Spacer(Modifier.width(8.dp))
                        Text("RESUME")
                    }
                    OutlinedButton(
                        onClick = vm::reset,
                        shape   = RoundedCornerShape(40.dp),
                        modifier = Modifier.width(130.dp).height(52.dp)
                    ) {
                        Icon(Icons.Outlined.Refresh, "Reset")
                        Spacer(Modifier.width(8.dp))
                        Text("RESET")
                    }
                }
            }
        }
    }
}

@Composable
fun TimeInputRow(
    hours: Int, minutes: Int, seconds: Int,
    onH: (Int) -> Unit, onM: (Int) -> Unit, onS: (Int) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TimeInputField("HH", hours,   0, 23,  onH)
        TimeSep()
        TimeInputField("MM", minutes, 0, 59,  onM)
        TimeSep()
        TimeInputField("SS", seconds, 0, 59,  onS)
    }
}

@Composable
fun TimeInputField(label: String, value: Int, min: Int, max: Int, onChange: (Int) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(onClick = { if (value < max) onChange(value + 1) }) {
            Icon(Icons.Outlined.KeyboardArrowUp, "Up", tint = MaterialTheme.colorScheme.primary)
        }
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape  = RoundedCornerShape(12.dp),
            modifier = Modifier.size(80.dp)
        ) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text       = "%02d".format(value),
                    fontFamily = OrbitronFamily,
                    fontSize   = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color      = MaterialTheme.colorScheme.onSurface
                )
            }
        }
        IconButton(onClick = { if (value > min) onChange(value - 1) }) {
            Icon(Icons.Outlined.KeyboardArrowDown, "Down", tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun TimeSep() {
    Text(
        ":",
        fontFamily = OrbitronFamily,
        fontSize = 36.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(bottom = 24.dp)
    )
}

data class TimerState(
    val inputH: Int = 0,
    val inputM: Int = 5,
    val inputS: Int = 0,
    val active: Boolean = false,
    val running: Boolean = false,
    val totalSeconds: Int = 0,
    val remainingMs: Long = 0L
)
