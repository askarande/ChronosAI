package com.chronos.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.chronos.app.viewmodel.SettingsViewModel

// ── Cyber Teal (default) ─────────────────────────────────────────────────────
val CyberPrimary    = Color(0xFF00F5D4)
val CyberSecondary  = Color(0xFF7C3AED)
val CyberBackground = Color(0xFF0A0A0F)
val CyberSurface    = Color(0xFF12121A)
val CyberSurface2   = Color(0xFF1A1A28)

// ── Aurora ────────────────────────────────────────────────────────────────────
val AuroraPrimary    = Color(0xFFA855F7)
val AuroraSecondary  = Color(0xFF06B6D4)
val AuroraBackground = Color(0xFF0D0D1A)
val AuroraSurface    = Color(0xFF1A0A2E)

// ── Ember ─────────────────────────────────────────────────────────────────────
val EmberPrimary    = Color(0xFFF97316)
val EmberSecondary  = Color(0xFFFBBF24)
val EmberBackground = Color(0xFF1A0A00)
val EmberSurface    = Color(0xFF2A1000)

// ── Arctic ────────────────────────────────────────────────────────────────────
val ArcticPrimary    = Color(0xFF38BDF8)
val ArcticSecondary  = Color(0xFF818CF8)
val ArcticBackground = Color(0xFF0A0F1A)
val ArcticSurface    = Color(0xFF0F1729)

// ── Matrix ────────────────────────────────────────────────────────────────────
val MatrixPrimary    = Color(0xFF00FF41)
val MatrixSecondary  = Color(0xFF007A1E)
val MatrixBackground = Color(0xFF000000)
val MatrixSurface    = Color(0xFF001200)

fun buildDarkScheme(primary: Color, secondary: Color, bg: Color, surface: Color) =
    darkColorScheme(
        primary           = primary,
        onPrimary         = Color(0xFF000000),
        primaryContainer  = primary.copy(alpha = 0.15f),
        secondary         = secondary,
        onSecondary       = Color(0xFFFFFFFF),
        background        = bg,
        onBackground      = Color(0xFFE2E8F0),
        surface           = surface,
        onSurface         = Color(0xFFE2E8F0),
        surfaceVariant    = surface.copy(alpha = 0.7f),
        onSurfaceVariant  = Color(0xFF94A3B8),
        outline           = Color(0xFF334155),
        error             = Color(0xFFEF4444),
    )

enum class AppTheme { CYBER_TEAL, AURORA, EMBER, ARCTIC, MATRIX }

fun getColorScheme(theme: AppTheme) = when (theme) {
    AppTheme.CYBER_TEAL -> buildDarkScheme(CyberPrimary, CyberSecondary, CyberBackground, CyberSurface)
    AppTheme.AURORA     -> buildDarkScheme(AuroraPrimary, AuroraSecondary, AuroraBackground, AuroraSurface)
    AppTheme.EMBER      -> buildDarkScheme(EmberPrimary, EmberSecondary, EmberBackground, EmberSurface)
    AppTheme.ARCTIC     -> buildDarkScheme(ArcticPrimary, ArcticSecondary, ArcticBackground, ArcticSurface)
    AppTheme.MATRIX     -> buildDarkScheme(MatrixPrimary, MatrixSecondary, MatrixBackground, MatrixSurface)
}

@Composable
fun ChronosTheme(
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    content: @Composable () -> Unit
) {
    val settings by settingsViewModel.settings.collectAsState()
    val colorScheme = getColorScheme(settings.theme)

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = ChronosTypography,
        content     = content
    )
}
