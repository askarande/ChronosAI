package com.chronos.app.ui.alarm

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.chronos.app.ui.theme.OrbitronFamily
import com.chronos.app.viewmodel.AlarmViewModel

data class AlarmItem(
    val id: Int,
    val hour: Int,
    val minute: Int,
    val label: String,
    val days: Set<Int>,   // 0=Sun … 6=Sat
    val enabled: Boolean,
    val ringtone: String = "Default",
    val vibrate: Boolean = true,
    val snoozeMin: Int = 5
)

val DAY_LABELS = listOf("S", "M", "T", "W", "T", "F", "S")

@Composable
fun AlarmScreen(vm: AlarmViewModel = hiltViewModel()) {
    val alarms by vm.alarms.collectAsState()
    var showAdd by remember { mutableStateOf(false) }

    Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Alarms", style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground)
                FilledTonalIconButton(onClick = { showAdd = true }) {
                    Icon(Icons.Outlined.Add, "Add alarm")
                }
            }

            if (alarms.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("⏰", fontSize = 64.sp)
                        Spacer(Modifier.height(12.dp))
                        Text("No alarms set", style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = { showAdd = true }) { Text("Add Alarm") }
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(alarms, key = { it.id }) { alarm ->
                        AlarmCard(
                            alarm    = alarm,
                            onToggle = { vm.toggleAlarm(alarm.id) },
                            onDelete = { vm.deleteAlarm(alarm.id) },
                            onDayToggle = { day -> vm.toggleDay(alarm.id, day) }
                        )
                    }
                    item { Spacer(Modifier.height(80.dp)) }
                }
            }
        }

        // FAB
        FloatingActionButton(
            onClick = { showAdd = true },
            modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor   = Color.Black
        ) {
            Icon(Icons.Outlined.Add, "Add")
        }

        // Add Alarm bottom sheet
        if (showAdd) {
            AddAlarmBottomSheet(
                onDismiss = { showAdd = false },
                onSave    = { h, m, label, days ->
                    vm.addAlarm(h, m, label, days)
                    showAdd = false
                }
            )
        }
    }
}

@Composable
fun AlarmCard(
    alarm: AlarmItem,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    onDayToggle: (Int) -> Unit
) {
    val nextTrigger = getNextTrigger(alarm)

    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (alarm.enabled)
                MaterialTheme.colorScheme.surface
            else
                MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
        ),
        shape  = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        "%02d:%02d".format(alarm.hour, alarm.minute),
                        fontFamily = OrbitronFamily,
                        fontSize   = 40.sp,
                        fontWeight = FontWeight.Black,
                        color      = if (alarm.enabled) MaterialTheme.colorScheme.onSurface
                                     else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (alarm.label.isNotBlank()) {
                        Text(
                            alarm.label,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (alarm.enabled && nextTrigger.isNotEmpty()) {
                        Text(
                            nextTrigger,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Switch(
                        checked  = alarm.enabled,
                        onCheckedChange = { onToggle() },
                        colors   = SwitchDefaults.colors(
                            checkedThumbColor       = Color.Black,
                            checkedTrackColor       = MaterialTheme.colorScheme.primary,
                            uncheckedTrackColor     = MaterialTheme.colorScheme.outline
                        )
                    )
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Outlined.Delete, "Delete",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp))
                    }
                }
            }
            Spacer(Modifier.height(10.dp))
            // Day selector
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                DAY_LABELS.forEachIndexed { i, lbl ->
                    val selected = alarm.days.contains(i)
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(
                                if (selected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .then(
                                Modifier.clickable { onDayToggle(i) }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            lbl, fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selected) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(Modifier.weight(1f))
                // Ringtone indicator
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.VolumeUp, "Sound",
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.width(4.dp))
                    Text(alarm.ringtone, style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

fun getNextTrigger(alarm: AlarmItem): String {
    if (!alarm.enabled) return ""
    val cal = java.util.Calendar.getInstance()
    val nowMin = cal.get(java.util.Calendar.HOUR_OF_DAY) * 60 + cal.get(java.util.Calendar.MINUTE)
    val alarmMin = alarm.hour * 60 + alarm.minute
    val diffMin = if (alarmMin > nowMin) alarmMin - nowMin
                  else 24 * 60 - nowMin + alarmMin
    return when {
        diffMin < 60  -> "Rings in ${diffMin}m"
        diffMin < 1440-> "Rings in ${diffMin / 60}h ${diffMin % 60}m"
        else          -> "Rings tomorrow"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAlarmBottomSheet(
    onDismiss: () -> Unit,
    onSave: (Int, Int, String, Set<Int>) -> Unit
) {
    var hour   by remember { mutableStateOf(7) }
    var minute by remember { mutableStateOf(0) }
    var label  by remember { mutableStateOf("") }
    var days   by remember { mutableStateOf(setOf(1, 2, 3, 4, 5)) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor   = MaterialTheme.colorScheme.surface
    ) {
        Column(Modifier.padding(24.dp)) {
            Text("New Alarm", style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(20.dp))

            // Time picker row
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                NumberPicker(hour, 0, 23) { hour = it }
                Text(":", fontFamily = OrbitronFamily, fontSize = 40.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 8.dp))
                NumberPicker(minute, 0, 59) { minute = it }
            }

            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value    = label,
                onValueChange = { label = it },
                label    = { Text("Label") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(Modifier.height(16.dp))
            Text("REPEAT", style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                DAY_LABELS.forEachIndexed { i, lbl ->
                    FilterChip(
                        selected = days.contains(i),
                        onClick  = {
                            days = if (days.contains(i)) days - i else days + i
                        },
                        label    = { Text(lbl) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            Spacer(Modifier.height(24.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f).height(52.dp)) {
                    Text("Cancel")
                }
                Button(
                    onClick = { onSave(hour, minute, label, days) },
                    modifier = Modifier.weight(1f).height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) { Text("Save Alarm", color = Color.Black, fontWeight = FontWeight.Bold) }
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
fun NumberPicker(value: Int, min: Int, max: Int, onChange: (Int) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(onClick = { onChange(if (value < max) value + 1 else min) }) {
            Icon(Icons.Outlined.KeyboardArrowUp, "Up", tint = MaterialTheme.colorScheme.primary)
        }
        Text("%02d".format(value), fontFamily = OrbitronFamily, fontSize = 36.sp,
            fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurface)
        IconButton(onClick = { onChange(if (value > min) value - 1 else max) }) {
            Icon(Icons.Outlined.KeyboardArrowDown, "Down", tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

private fun Modifier.clickable(onClick: () -> Unit) =
    this.then(Modifier.then(androidx.compose.foundation.clickable(onClick = onClick)))
