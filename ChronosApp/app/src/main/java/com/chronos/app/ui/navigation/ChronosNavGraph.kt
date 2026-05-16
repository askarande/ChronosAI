package com.chronos.app.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.chronos.app.ui.clock.ClockScreen
import com.chronos.app.ui.stopwatch.StopwatchScreen
import com.chronos.app.ui.timer.TimerScreen
import com.chronos.app.ui.pomodoro.PomodoroScreen
import com.chronos.app.ui.worldclock.WorldClockScreen
import com.chronos.app.ui.alarm.AlarmScreen
import com.chronos.app.ui.music.MusicScreen
import com.chronos.app.ui.settings.SettingsScreen

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Clock      : Screen("clock",      "Clock",     Icons.Outlined.AccessTime)
    object Stopwatch  : Screen("stopwatch",  "Watch",     Icons.Outlined.Timer)
    object Timer      : Screen("timer",      "Timer",     Icons.Outlined.HourglassBottom)
    object Pomodoro   : Screen("pomodoro",   "Focus",     Icons.Outlined.SelfImprovement)
    object World      : Screen("world",      "World",     Icons.Outlined.Public)
    object Alarm      : Screen("alarm",      "Alarm",     Icons.Outlined.Alarm)
    object Music      : Screen("music",      "Music",     Icons.Outlined.MusicNote)
    object Settings   : Screen("settings",   "Settings",  Icons.Outlined.Settings)
}

val bottomNavItems = listOf(
    Screen.Clock, Screen.Stopwatch, Screen.Timer,
    Screen.Pomodoro, Screen.World, Screen.Alarm
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChronosNavGraph() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = androidx.compose.ui.unit.Dp(0f)
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                bottomNavItems.forEach { screen ->
                    val selected = currentDestination?.hierarchy
                        ?.any { it.route == screen.route } == true

                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = screen.icon,
                                contentDescription = screen.label
                            )
                        },
                        label = {
                            Text(
                                text = screen.label,
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        selected = selected,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor   = MaterialTheme.colorScheme.primary,
                            selectedTextColor   = MaterialTheme.colorScheme.primary,
                            indicatorColor      = MaterialTheme.colorScheme.primaryContainer,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    )
                }

                // Music icon
                NavigationBarItem(
                    icon = { Icon(Screen.Music.icon, Screen.Music.label) },
                    label = { Text(Screen.Music.label, style = MaterialTheme.typography.labelSmall) },
                    selected = navController.currentBackStackEntryAsState()
                        .value?.destination?.route == Screen.Music.route,
                    onClick = { navController.navigate(Screen.Music.route) }
                )

                // Settings icon
                NavigationBarItem(
                    icon = { Icon(Screen.Settings.icon, Screen.Settings.label) },
                    label = { Text(Screen.Settings.label, style = MaterialTheme.typography.labelSmall) },
                    selected = navController.currentBackStackEntryAsState()
                        .value?.destination?.route == Screen.Settings.route,
                    onClick = { navController.navigate(Screen.Settings.route) }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Clock.route,
            modifier = Modifier.padding(innerPadding),
            enterTransition = {
                slideInHorizontally(tween(300)) + fadeIn(tween(300))
            },
            exitTransition = {
                slideOutHorizontally(tween(300)) + fadeOut(tween(300))
            }
        ) {
            composable(Screen.Clock.route)     { ClockScreen() }
            composable(Screen.Stopwatch.route) { StopwatchScreen() }
            composable(Screen.Timer.route)     { TimerScreen() }
            composable(Screen.Pomodoro.route)  { PomodoroScreen() }
            composable(Screen.World.route)     { WorldClockScreen() }
            composable(Screen.Alarm.route)     { AlarmScreen() }
            composable(Screen.Music.route)     { MusicScreen() }
            composable(Screen.Settings.route)  { SettingsScreen() }
        }
    }
}
