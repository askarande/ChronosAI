package com.chronos.app.ui.stopwatch

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.chronos.app.ui.theme.OrbitronFamily
import com.chronos.app.ui.theme.SpaceMonoFamily
import com.chronos.app.viewmodel.StopwatchViewModel

@Composable
fun StopwatchScreen(vm: StopwatchViewModel = hiltViewModel()) {
    val state by vm.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(24.dp))

        // Big time display
        BigTimeDisplay(state.elapsed, state.running)

        Spacer(Modifier.height(32.dp))

        // Control buttons
        Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (state.running) {
                CircleButton(
                    icon = Icons.Outlined.Flag,
                    label = "LAP",
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    onClick = vm::lap
                )
                CircleButton(
                    icon = Icons.Outlined.Stop,
                    label = "STOP",
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = Color.White,
                    size = 80.dp,
                    onClick = vm::stop
                )
            } else {
                CircleButton(
                    icon = Icons.Outlined.Refresh,
                    label = "RESET",
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    enabled = state.elapsed > 0,
                    onClick = vm::reset
                )
                CircleButton(
                    icon = if (state.elapsed > 0) Icons.Outlined.PlayArrow else Icons.Outlined.PlayArrow,
                    label = if (state.elapsed > 0) "RESUME" else "START",
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.Black,
                    size = 80.dp,
                    onClick = vm::start
                )
            }
        }

        Spacer(Modifier.height(28.dp))

        // Laps
        if (state.laps.isNotEmpty()) {
            Text(
                "LAPS",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(Modifier.height(8.dp))
            val fastLap = state.laps.minByOrNull { it.splitMs }?.splitMs
            val slowLap = state.laps.maxByOrNull { it.splitMs }?.splitMs
            LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                items(state.laps.reversed()) { lap ->
                    LapRow(
                        lap = lap,
                        isFastest = lap.splitMs == fastLap && state.laps.size > 1,
                        isSlowest = lap.splitMs == slowLap && state.laps.size > 1
                    )
                }
            }
        }
    }
}

@Composable
fun BigTimeDisplay(elapsedMs: Long, running: Boolean) {
    val totalSec = elapsedMs / 1000
    val h = totalSec / 3600
    val m = (totalSec % 3600) / 60
    val s = totalSec % 60
    val ms = (elapsedMs % 1000) / 10

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.Bottom) {
            if (h > 0) {
                Text(
                    text = "%02d:".format(h),
                    fontFamily = OrbitronFamily,
                    fontSize = 52.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Text(
                text = "%02d:%02d".format(m, s),
                fontFamily = OrbitronFamily,
                fontSize = 64.sp,
                fontWeight = FontWeight.Black,
                color = if (running) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = ".%02d".format(ms),
                fontFamily = OrbitronFamily,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 6.dp)
            )
        }
        AnimatedVisibility(running) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    Modifier
                        .size(8.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                )
                Text(
                    "RUNNING",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun CircleButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    containerColor: Color,
    contentColor: Color,
    size: androidx.compose.ui.unit.Dp = 64.dp,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        FloatingActionButton(
            onClick = onClick,
            modifier = Modifier.size(size),
            shape = CircleShape,
            containerColor = if (enabled) containerColor else MaterialTheme.colorScheme.surface,
            contentColor = if (enabled) contentColor else MaterialTheme.colorScheme.onSurfaceVariant,
            elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp)
        ) {
            Icon(icon, label, modifier = Modifier.size(size * 0.4f))
        }
        Spacer(Modifier.height(6.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun LapRow(lap: LapEntry, isFastest: Boolean, isSlowest: Boolean) {
    val accent = when {
        isFastest -> MaterialTheme.colorScheme.primary
        isSlowest -> MaterialTheme.colorScheme.error
        else      -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "LAP ${lap.index}",
                fontFamily = SpaceMonoFamily,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                formatMs(lap.splitMs),
                fontFamily = SpaceMonoFamily,
                fontSize = 14.sp,
                color = accent
            )
            Text(
                formatMs(lap.totalMs),
                fontFamily = SpaceMonoFamily,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (isFastest) Text("⚡ BEST", style = MaterialTheme.typography.labelSmall, color = accent)
            else if (isSlowest) Text("🐢 SLOW", style = MaterialTheme.typography.labelSmall, color = accent)
            else Spacer(Modifier.width(48.dp))
        }
    }
}

fun formatMs(ms: Long): String {
    val s = ms / 1000
    val m = s / 60
    val sec = s % 60
    val millis = (ms % 1000) / 10
    return "%02d:%02d.%02d".format(m, sec, millis)
}

data class LapEntry(val index: Int, val totalMs: Long, val splitMs: Long)
data class StopwatchState(
    val running: Boolean = false,
    val elapsed: Long = 0L,
    val laps: List<LapEntry> = emptyList()
)
