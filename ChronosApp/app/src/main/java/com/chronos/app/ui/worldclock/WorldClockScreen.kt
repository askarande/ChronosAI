package com.chronos.app.ui.worldclock

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.chronos.app.ui.theme.OrbitronFamily
import com.chronos.app.ui.theme.SpaceMonoFamily
import com.chronos.app.viewmodel.WorldClockViewModel
import java.text.SimpleDateFormat
import java.util.*

data class WorldCity(
    val city: String,
    val country: String,
    val timezone: String,
    val flag: String,
    val weatherEmoji: String = "☀️",
    val tempC: Int = 22
)

val DEFAULT_CITIES = listOf(
    WorldCity("Mumbai",     "India",       "Asia/Kolkata",        "🇮🇳", "☀️", 32),
    WorldCity("New York",   "USA",         "America/New_York",    "🇺🇸", "🌤", 18),
    WorldCity("London",     "UK",          "Europe/London",       "🇬🇧", "🌧", 14),
    WorldCity("Tokyo",      "Japan",       "Asia/Tokyo",          "🇯🇵", "⛅", 21),
    WorldCity("Dubai",      "UAE",         "Asia/Dubai",          "🇦🇪", "☀️", 38),
    WorldCity("Sydney",     "Australia",   "Australia/Sydney",    "🇦🇺", "🌤", 25),
    WorldCity("Singapore",  "Singapore",   "Asia/Singapore",      "🇸🇬", "⛈", 30),
    WorldCity("Paris",      "France",      "Europe/Paris",        "🇫🇷", "🌦", 16),
    WorldCity("Los Angeles","USA",         "America/Los_Angeles", "🇺🇸", "☀️", 22),
    WorldCity("Moscow",     "Russia",      "Europe/Moscow",       "🇷🇺", "❄️", -2),
)

@Composable
fun WorldClockScreen(vm: WorldClockViewModel = hiltViewModel()) {
    val cities by vm.cities.collectAsState()
    var now by remember { mutableStateOf(System.currentTimeMillis()) }

    // Tick every second
    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(1000)
            now = System.currentTimeMillis()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header with search
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "World Clock",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            IconButton(onClick = { vm.showSearch() }) {
                Icon(
                    Icons.Outlined.Search, "Search city",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(cities, key = { it.city }) { city ->
                WorldCityCard(city = city, now = now)
            }
            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

@Composable
fun WorldCityCard(city: WorldCity, now: Long) {
    val tz = TimeZone.getTimeZone(city.timezone)
    val timeFmt  = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).apply { timeZone = tz }
    val dateFmt  = SimpleDateFormat("EEE, d MMM", Locale.getDefault()).apply { timeZone = tz }
    val date     = Date(now)
    val timeStr  = timeFmt.format(date)
    val dateStr  = dateFmt.format(date)

    // Offset vs local
    val localTz   = TimeZone.getDefault()
    val offsetMs  = tz.getOffset(now) - localTz.getOffset(now)
    val offsetH   = offsetMs / 3_600_000
    val offsetM   = (Math.abs(offsetMs) % 3_600_000) / 60_000
    val offsetStr = when {
        offsetH == 0 && offsetM == 0 -> "Local"
        offsetM == 0 -> if (offsetH > 0) "+${offsetH}h" else "${offsetH}h"
        else -> if (offsetH >= 0) "+${offsetH}h ${offsetM}m" else "${offsetH}h ${offsetM}m"
    }

    val isDay = run {
        val cal = Calendar.getInstance(tz)
        cal.timeInMillis = now
        cal.get(Calendar.HOUR_OF_DAY) in 6..19
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: flag + city info
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(city.flag, fontSize = 32.sp, modifier = Modifier.padding(end = 12.dp))
                Column {
                    Text(
                        city.city,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        city.country,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            dateStr,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            offsetStr,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Right: time + weather
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    timeStr,
                    fontFamily = OrbitronFamily,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDay) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.secondary
                )
                Spacer(Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(city.weatherEmoji, fontSize = 14.sp)
                    Text(
                        "${city.tempC}°C",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        if (isDay) "☀" else "🌙",
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
