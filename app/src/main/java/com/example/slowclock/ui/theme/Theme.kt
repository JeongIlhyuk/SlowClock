// app/src/main/java/com/example/slowclock/ui/theme/Theme.kt
package com.example.slowclock.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// 다크 테마 (고대비)
private val DarkColorScheme = darkColorScheme(
    primary = PrimaryLight,
    onPrimary = Surface,
    primaryContainer = PrimaryDark,
    onPrimaryContainer = Surface,

    secondary = SuccessLight,
    onSecondary = Surface,
    secondaryContainer = Success,
    onSecondaryContainer = Surface,

    tertiary = WarningLight,
    onTertiary = Surface,
    tertiaryContainer = Warning,
    onTertiaryContainer = Surface,

    background = Color(0xFF121212),
    onBackground = Color(0xFFE0E0E0),
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFE0E0E0),
    surfaceVariant = Color(0xFF2C2C2C),
    onSurfaceVariant = Color(0xFFBDBDBD),

    error = Color(0xFFEF5350),
    onError = Surface,
    errorContainer = Color(0xFFB71C1C),
    onErrorContainer = Color(0xFFFFCDD2)
)

// 라이트 테마 (고대비, 접근성 강화)
private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = Surface,
    primaryContainer = Color(0xFFBBDEFB),
    onPrimaryContainer = PrimaryDark,

    secondary = Success,
    onSecondary = Surface,
    secondaryContainer = SuccessSurface,
    onSecondaryContainer = Success,

    tertiary = Warning,
    onTertiary = Surface,
    tertiaryContainer = WarningSurface,
    onTertiaryContainer = Warning,

    background = Background,
    onBackground = TextPrimary,
    surface = Surface,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = TextSecondary,

    error = Error,
    onError = Surface,
    errorContainer = ErrorSurface,
    onErrorContainer = Error,

    outline = Color(0xFFBDBDBD),
    outlineVariant = Color(0xFFE0E0E0)
)

@Composable
fun SlowClockTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // 접근성을 위해 동적 색상 기본값을 false로 변경
    dynamicColor: Boolean = false, // true → false 변경
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}