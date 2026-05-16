package com.chronos.app.ui.music

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.chronos.app.viewmodel.MusicViewModel

data class Track(
    val id: Int,
    val title: String,
    val artist: String,
    val durationSec: Int,
    val category: String,
    val emoji: String
)

val FOCUS_TRACKS = listOf(
    Track(1, "Deep Focus",         "Ambient Flows",    240, "Focus",  "🎯"),
    Track(2, "Lo-Fi Study",        "Chill Beats",      185, "Lo-Fi",  "📚"),
    Track(3, "Concentration",      "Brain Waves",      320, "Binaural","🧠"),
    Track(4, "Night Mode",         "Dark Ambient",     275, "Ambient","🌙"),
    Track(5, "Morning Light",      "Nature Sounds",    210, "Nature", "🌅"),
    Track(6, "Rain on Glass",      "Weather Sounds",   360, "Nature", "🌧"),
    Track(7, "Forest Meditation",  "Zen Studio",       300, "Zen",    "🌲"),
    Track(8, "Alpha Waves",        "BrainSync",        420, "Binaural","⚡"),
    Track(9, "Cafe Study",         "Urban Sounds",     180, "Lo-Fi",  "☕"),
    Track(10,"Flow State",         "Chronos AI",       600, "AI",     "🤖"),
)

@Composable
fun MusicScreen(vm: MusicViewModel = hiltViewModel()) {
    val state by vm.state.collectAsState()
    val primary = MaterialTheme.colorScheme.primary

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Now playing hero
        NowPlayingHero(state = state, onPlayPause = vm::togglePlay, onNext = vm::next, onPrev = vm::prev)

        // Progress
        Column(Modifier.padding(horizontal = 24.dp)) {
            LinearProgressIndicator(
                progress = { state.progressFraction },
                modifier = Modifier.fillMaxWidth().height(3.dp).clip(RoundedCornerShape(2.dp)),
                color    = primary,
                trackColor = MaterialTheme.colorScheme.surface
            )
            Row(
                Modifier.fillMaxWidth().padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(formatSec(state.positionSec), style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(formatSec(state.currentTrack.durationSec), style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        Spacer(Modifier.height(8.dp))

        // Extra controls: shuffle, repeat, timer
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(onClick = vm::toggleShuffle) {
                Icon(Icons.Outlined.Shuffle, "Shuffle",
                    tint = if (state.shuffle) primary else MaterialTheme.colorScheme.onSurfaceVariant)
            }
            IconButton(onClick = vm::toggleRepeat) {
                Icon(Icons.Outlined.Repeat, "Repeat",
                    tint = if (state.repeat) primary else MaterialTheme.colorScheme.onSurfaceVariant)
            }
            IconButton(onClick = { }) {
                Icon(Icons.Outlined.Timer, "Sleep Timer",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            IconButton(onClick = { }) {
                Icon(Icons.Outlined.Equalizer, "EQ",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

        // Track list
        Text(
            "PLAYLIST",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )
        LazyColumn {
            itemsIndexed(FOCUS_TRACKS) { _, track ->
                TrackRow(
                    track    = track,
                    isPlaying = track.id == state.currentTrack.id && state.playing,
                    isCurrent = track.id == state.currentTrack.id,
                    onClick  = { vm.playTrack(track) }
                )
            }
            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

@Composable
fun NowPlayingHero(
    state: MusicState,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrev: () -> Unit
) {
    val primary = MaterialTheme.colorScheme.primary
    val rotation by rememberInfiniteTransition(label = "disc").animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(8000, easing = LinearEasing)), label = "rot"
    )

    Box(
        Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    listOf(
                        primary.copy(alpha = 0.15f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
            .padding(24.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Spinning disc art
            Box(
                Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(MaterialTheme.colorScheme.surface, primary.copy(alpha = 0.3f))
                        )
                    )
                    .rotate(if (state.playing) rotation else 0f),
                contentAlignment = Alignment.Center
            ) {
                Text(state.currentTrack.emoji, fontSize = 36.sp)
                Box(
                    Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.background)
                )
            }

            Spacer(Modifier.width(16.dp))

            // Track info + controls
            Column(Modifier.weight(1f)) {
                Text(state.currentTrack.title, style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                Text(state.currentTrack.artist, style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(4.dp))
                Text(state.currentTrack.category,
                    style = MaterialTheme.typography.labelSmall,
                    color = primary,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(primary.copy(alpha = 0.12f))
                        .padding(horizontal = 8.dp, vertical = 2.dp))
                Spacer(Modifier.height(12.dp))

                // Transport row
                Row(verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(onClick = onPrev, modifier = Modifier.size(40.dp)) {
                        Icon(Icons.Outlined.SkipPrevious, "Prev", tint = MaterialTheme.colorScheme.onSurface)
                    }
                    FloatingActionButton(
                        onClick        = onPlayPause,
                        modifier       = Modifier.size(52.dp),
                        shape          = CircleShape,
                        containerColor = primary,
                        contentColor   = Color.Black,
                        elevation      = FloatingActionButtonDefaults.elevation(0.dp)
                    ) {
                        Icon(
                            if (state.playing) Icons.Outlined.Pause else Icons.Outlined.PlayArrow,
                            "Play",
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    IconButton(onClick = onNext, modifier = Modifier.size(40.dp)) {
                        Icon(Icons.Outlined.SkipNext, "Next", tint = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
        }
    }
}

@Composable
fun TrackRow(track: Track, isPlaying: Boolean, isCurrent: Boolean, onClick: () -> Unit) {
    val primary = MaterialTheme.colorScheme.primary
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .background(if (isCurrent) primary.copy(alpha = 0.07f) else Color.Transparent)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (isCurrent) primary.copy(alpha = 0.2f)
                        else MaterialTheme.colorScheme.surface
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isPlaying) {
                    // Animated bars
                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp), verticalAlignment = Alignment.Bottom) {
                        repeat(3) { i ->
                            val h by rememberInfiniteTransition(label = "bar$i").animateFloat(
                                initialValue = 4f, targetValue = 16f,
                                animationSpec = infiniteRepeatable(tween(300 + i * 100), RepeatMode.Reverse), label = "h$i"
                            )
                            Box(Modifier.width(3.dp).height(h.dp).background(primary, RoundedCornerShape(2.dp)))
                        }
                    }
                } else {
                    Text(track.emoji, fontSize = 18.sp)
                }
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(track.title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isCurrent) primary else MaterialTheme.colorScheme.onSurface,
                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal)
                Text(track.artist,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Text(formatSec(track.durationSec),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

fun formatSec(sec: Int) = "%d:%02d".format(sec / 60, sec % 60)

data class MusicState(
    val currentTrack: Track = FOCUS_TRACKS[0],
    val playing: Boolean = false,
    val positionSec: Int = 0,
    val shuffle: Boolean = false,
    val repeat: Boolean = false
) {
    val progressFraction: Float
        get() = if (currentTrack.durationSec > 0) positionSec.toFloat() / currentTrack.durationSec else 0f
}
