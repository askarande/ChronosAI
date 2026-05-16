# ⬡ CHRONOS AI — Advanced Android Clock App

A modern, AI-powered flip clock Android application built with Jetpack Compose, Kotlin, and Hilt.

---

## ✨ Features

| Feature | Details |
|---|---|
| **Flip Clock** | 3D split-flap animation, smooth 250ms flip |
| **Analog Clock** | Canvas-drawn, smooth second-hand sweep |
| **Binary Clock** | Geek-style binary LED display |
| **Zen Clock** | Minimal breathing animation |
| **Stopwatch** | 10ms precision, lap tracking, fastest/slowest lap |
| **Timer** | Ring progress, presets, notification on completion |
| **Pomodoro / Focus** | 25/5/15 cycles, session dots, AI tips |
| **World Clock** | 10+ cities, live time/date/offset/weather |
| **Alarm** | Repeating days, custom ringtone, snooze, boot-persistent |
| **Music Player** | 10 ambient focus tracks, disc animation, EQ |
| **Floating Clock** | Overlay window (SYSTEM_ALERT_WINDOW) |
| **Home Widget** | Glance API clock widget |
| **Themes** | Cyber Teal, Aurora, Ember, Arctic, Matrix |
| **AI Insights** | Focus score, streak, productivity tips |
| **Settings** | DataStore persisted, 24h/12h, fonts, language |
| **Battery %** | Live battery indicator in top bar |
| **Weather** | OpenWeatherMap integration per city |
| **Orientation** | Sensor-driven portrait + landscape |
| **Multi-language** | English, Hindi, Marathi, Tamil + more |

---

## 🛠 Tech Stack

```
Kotlin 1.9          Jetpack Compose     Material3
Hilt 2.48           Room 2.6            DataStore
WorkManager         AlarmManager        Foreground Services
Retrofit 2.9        OkHttp              Glance Widgets
Accompanist         Lottie              Vico Charts
Coroutines + Flow   Navigation Compose  Splash Screen API
```

---

## 🚀 Setup Instructions

### 1. Prerequisites
- **Android Studio Hedgehog** (2023.1.1) or newer
- **JDK 17**
- **Android SDK 34** (API 34)
- **Kotlin 1.9.0**

### 2. Clone / Open Project
```bash
# If using Git
git clone https://github.com/yourname/ChronosAI.git
cd ChronosAI

# OR just open the ChronosApp folder directly in Android Studio:
File → Open → select ChronosApp/
```

### 3. Add Fonts (Required)
Download and place these fonts in `app/src/main/res/font/`:

| File name | Download from |
|---|---|
| `orbitron_regular.ttf` | [Google Fonts – Orbitron](https://fonts.google.com/specimen/Orbitron) |
| `orbitron_medium.ttf` | Same |
| `orbitron_bold.ttf` | Same |
| `orbitron_black.ttf` | Same |
| `spacemono_regular.ttf` | [Google Fonts – Space Mono](https://fonts.google.com/specimen/Space+Mono) |
| `spacemono_bold.ttf` | Same |
| `rajdhani_light.ttf` | [Google Fonts – Rajdhani](https://fonts.google.com/specimen/Rajdhani) |
| `rajdhani_medium.ttf` | Same |
| `rajdhani_bold.ttf` | Same |

> **Shortcut:** In Android Studio → res → font → right-click → New → Font resource file.
> Or use **Tools → Resource Manager → Font → +**

### 4. Add Weather API Key (Optional)
Sign up at [openweathermap.org](https://openweathermap.org/api) (free tier), then in `local.properties`:
```
WEATHER_API_KEY=your_key_here
```
And in `app/build.gradle` under `defaultConfig`:
```groovy
buildConfigField "String", "WEATHER_API_KEY", "\"${properties['WEATHER_API_KEY']}\""
```

### 5. Sync & Build
```
File → Sync Project with Gradle Files
Build → Make Project   (Ctrl+F9 / Cmd+F9)
Run → Run 'app'        (Shift+F10 / Ctrl+R)
```

### 6. Grant Permissions at Runtime
The app will request:
- **Exact Alarm** – for precise alarm scheduling
- **Notifications** – for alarm/timer alerts
- **Overlay** – for the floating clock window
- **Location** – for local weather (optional)

---

## 📁 Project Structure

```
app/src/main/java/com/chronos/app/
├── ChronosApplication.kt          # Hilt app + notification channels
├── MainActivity.kt                # Edge-to-edge Compose entry point
├── di/
│   └── AppModule.kt               # Hilt DI bindings
├── ui/
│   ├── theme/
│   │   ├── Theme.kt               # 5 color themes
│   │   └── Typography.kt          # Orbitron / SpaceMono / Rajdhani
│   ├── navigation/
│   │   └── ChronosNavGraph.kt     # Bottom nav + NavHost
│   ├── clock/
│   │   └── ClockScreen.kt         # Flip, Analog, Binary, Zen
│   ├── stopwatch/
│   │   └── StopwatchScreen.kt     # Laps, fastest/slowest
│   ├── timer/
│   │   └── TimerScreen.kt         # Ring progress, presets
│   ├── pomodoro/
│   │   └── PomodoroScreen.kt      # Session dots, AI tips
│   ├── worldclock/
│   │   └── WorldClockScreen.kt    # 10 cities, live offset
│   ├── alarm/
│   │   └── AlarmScreen.kt         # Repeating alarm, bottom sheet
│   ├── music/
│   │   └── MusicScreen.kt         # 10 ambient tracks, disc art
│   └── settings/
│       └── SettingsScreen.kt      # Themes, fonts, language
├── viewmodel/
│   ├── ClockViewModel.kt
│   ├── StopwatchViewModel.kt
│   ├── TimerViewModel.kt
│   ├── PomodoroViewModel.kt
│   ├── WorldClockViewModel.kt
│   ├── AlarmViewModel.kt
│   ├── MusicViewModel.kt
│   └── SettingsViewModel.kt
├── service/
│   └── FloatingClockService.kt    # Overlay floating clock
└── receiver/
    └── AlarmReceiver.kt           # Alarm + Boot receivers
```

---

## 🔧 Extend the App

### Add a real Room DB for alarms
```kotlin
@Entity(tableName = "alarms")
data class AlarmEntity(
    @PrimaryKey val id: Int,
    val hour: Int, val minute: Int,
    val label: String, val days: String, val enabled: Boolean
)

@Dao interface AlarmDao {
    @Query("SELECT * FROM alarms") fun getAll(): Flow<List<AlarmEntity>>
    @Insert(onConflict = REPLACE) suspend fun insert(alarm: AlarmEntity)
    @Delete suspend fun delete(alarm: AlarmEntity)
}
```

### Add Weather API (Retrofit)
```kotlin
interface WeatherApi {
    @GET("weather")
    suspend fun getCurrent(
        @Query("q")    city: String,
        @Query("appid") key: String,
        @Query("units") units: String = "metric"
    ): WeatherResponse
}
```

### Add Glance Widget
```kotlin
class ClockWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            Text(SimpleDateFormat("HH:mm").format(Date()))
        }
    }
}
```

---

## 📱 Minimum Requirements
- Android 7.0 (API 24) and above
- ~15 MB installed size
- No ads, no trackers, no unnecessary permissions

---

## 📄 License
MIT — free to use, modify, and distribute.
