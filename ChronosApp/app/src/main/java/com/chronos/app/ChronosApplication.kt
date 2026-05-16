package com.chronos.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class ChronosApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NotificationManager::class.java)

            // Alarm channel
            NotificationChannel(
                CHANNEL_ALARM, "Alarms",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alarm notifications"
                enableVibration(true)
                setShowBadge(true)
                manager.createNotificationChannel(this)
            }

            // Timer channel
            NotificationChannel(
                CHANNEL_TIMER, "Timers",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Timer notifications"
                enableVibration(true)
                manager.createNotificationChannel(this)
            }

            // Floating clock channel
            NotificationChannel(
                CHANNEL_FLOATING, "Floating Clock",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Floating clock overlay"
                manager.createNotificationChannel(this)
            }

            // Music channel
            NotificationChannel(
                CHANNEL_MUSIC, "Music Player",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Focus music player"
                manager.createNotificationChannel(this)
            }
        }
    }

    companion object {
        const val CHANNEL_ALARM = "chronos_alarm"
        const val CHANNEL_TIMER = "chronos_timer"
        const val CHANNEL_FLOATING = "chronos_floating"
        const val CHANNEL_MUSIC = "chronos_music"
    }
}
