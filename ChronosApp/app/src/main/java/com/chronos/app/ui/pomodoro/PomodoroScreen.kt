package com.chronos.app.ui.pomodoro

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.chronos.app.ui.theme.OrbitronFamily
import com.chronos.app.viewmodel.PomodoroViewModel

enum class PomodoroMode(val label: String, val seconds: Int, val emoji: String) {
    FOCUS("Focus 25", 25 * 60, "🎯"),
    SHORT("Short Break", 5 * 60, "☕"),
    LONG("Long Break", 15 * 60, "🌿"),
    CUSTOM("Custom", 0, "⚙️")
}

@Composable
fun PomodoroScreen(vm: PomodoroViewModel = hiltViewModel()) {
    val state by vm.state.collectAsState()
    val primary    = MaterialTheme.colorScheme.primary
    val secondary  = MaterialTheme.colorScheme.secondary
    val bg         = MaterialTheme.colorScheme.background

    val ringColor = when (state.mode) {
        PomodoroMode.FOCUS  -> primary
        PomodoroMode.SHORT  -> secondary
        PomodoroMode.LONG   -> MaterialTheme.colorScheme.tertiary
        PomodoroMode.CUSTOM -> primary
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(8.dp))

        // Mode selector
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            PomodoroMode.values().take(3).forEach { mode ->
                FilterChip(
                    selected = state.mode == mode,
                    onClick  = { vm.setMode(mode) },
                    label    = {
                        Text("${mode.emoji} ${mode.label}", style = MaterialTheme.typography.labelSmall)
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ringColor,
                        selectedLabelColor     = MaterialTheme.colorScheme.onPrimary,
                        containerColor         = MaterialTheme.colorScheme.surface,
                        labelColor             = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(Modifier.height(32.dp))

        // Circular ring timer
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(260.dp)) {
            val progress = if (state.totalSeconds > 0)
                state.remainingMs.toFloat() / (state.totalSeconds * 1000f) else 1f

            Canvas(Modifier.fillMaxSize()) {
                val stroke = 20f
                val inset  = stroke / 2
                drawArc(
                    color      = ringColor.copy(alpha = 0.12f),
                    startAngle = -90f, sweepAngle = 360f, useCenter = false,
                    topLeft    = Offset(inset, inset),
                    size       = Size(size.width - stroke, size.height - stroke),
                    style      = Stroke(stroke, cap = StrokeCap.Round)
                )
                drawArc(
                    color      = ringColor,
                    startAngle = -90f,
                    sweepAngle = 360f * progress,
                    useCenter  = false,
                    topLeft    = Offset(inset, inset),
                    size       = Size(size.width - stroke, size.height - stroke),
                    style      = Stroke(stroke, cap = StrokeCap.Round)
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val rem  = state.remainingMs / 1000
                val m    = rem / 60
                val s    = rem % 60
                Text(
                    text       = "%02d:%02d".format(m, s),
                    fontFamily = OrbitronFamily,
                    fontSize   = 56.sp,
                    fontWeight = FontWeight.Black,
                    color      = ringColor
                )
                Text(
                    text  = state.mode.label.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        // Session dots
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            repeat(state.maxSessions) { i ->
                SessionDot(
                    done    = i < state.completedSessions,
                    current = i == state.completedSessions && state.running,
                    color   = primary
                )
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(
            "Session ${minOf(state.completedSessions + 1, state.maxSessions)} of ${state.maxSessions}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(28.dp))

        // Control buttons
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            if (state.running) {
                OutlinedButton(
                    onClick = vm::pause,
                    shape   = RoundedCornerShape(40.dp),
                    modifier = Modifier.height(52.dp).width(130.dp)
                ) {
                    Icon(Icons.Outlined.Pause, "Pause")
                    Spacer(Modifier.width(8.dp))
                    Text("PAUSE")
                }
            } else {
                Button(
                    onClick = vm::start,
                    colors  = ButtonDefaults.buttonColors(containerColor = ringColor),
                    shape   = RoundedCornerShape(40.dp),
                    modifier = Modifier.height(52.dp).width(130.dp)
                ) {
                    Icon(Icons.Outlined.PlayArrow, "Start")
                    Spacer(Modifier.width(8.dp))
                    Text(if (state.remainingMs < state.totalSeconds * 1000L && state.remainingMs > 0) "RESUME" else "START")
                }
            }
            OutlinedButton(
                onClick = vm::reset,
                shape   = RoundedCornerShape(40.dp),
                modifier = Modifier.height(52.dp).width(130.dp)
            ) {
                Icon(Icons.Outlined.Refresh, "Reset")
                Spacer(Modifier.width(8.dp))
                Text("RESET")
            }
        }

        Spacer(Modifier.height(28.dp))

        // AI Tip card
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape  = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                Text("💡", fontSize = 20.sp, modifier = Modifier.padding(end = 10.dp, top = 2.dp))
                Column {
                    Text(
                        "AI Insight",
                        style = MaterialTheme.typography.titleLarge,
                        color = primary
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Your peak focus window is 9 AM – 12 PM based on your usage pattern. " +
                        "Schedule deep study sessions in this window for SSC/UPSC prep. " +
                        "After 4 Pomodoros, take a 30-min break for optimal retention.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun SessionDot(done: Boolean, current: Boolean, color: androidx.compose.ui.graphics.Color) {
    val pulse by rememberInfiniteTransition(label = "dot").animateFloat(
        initialValue = 1f, targetValue = 1.4f,
        animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse), label = "dotPulse"
    )
    Box(
        Modifier
            .size(if (current) (12 * pulse).dp else 12.dp)
            .background(
                color = if (done) color else if (current) color.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline,
                shape = CircleShape
            )
    )
}

data class PomodoroState(
    val mode: PomodoroMode = PomodoroMode.FOCUS,
    val running: Boolean = false,
    val totalSeconds: Int = PomodoroMode.FOCUS.seconds,
    val remainingMs: Long = PomodoroMode.FOCUS.seconds * 1000L,
    val completedSessions: Int = 0,
    val maxSessions: Int = 4
)
