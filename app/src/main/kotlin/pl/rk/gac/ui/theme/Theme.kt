package pl.rk.gac.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

/**
 * Light color scheme definition for the application theme.
 *
 * This color scheme is used when the app is in light mode or when dynamic coloring is unavailable.
 */
val LightColorScheme = lightColorScheme(
    primary = MagentaPrimary,
    onPrimary = MagentaOnPrimary,
    secondary = MagentaSecondary,
    background = Color(0xFFFDFDFD),
    surface = Color(0xFFFFFFFF),
)

/**
 * Dark color scheme definition for the application theme.
 *
 * This color scheme is used when the app is in dark mode or when dynamic coloring is unavailable.
 */
val DarkColorScheme = darkColorScheme(
    primary = MagentaPrimary,
    onPrimary = MagentaOnPrimary,
    secondary = MagentaSecondary,
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
)

/**
 * Main theme composable for the Gospel A Caso application.
 *
 * This function applies the appropriate theme based on the current system settings and device capabilities.
 * It supports dynamic coloring on Android 12+ and handles the dark/light theme switching.
 *
 * @param darkTheme Whether to use the dark theme. Defaults to the system setting.
 * @param dynamicColor Whether to use Material You dynamic coloring on supported devices. Defaults to true.
 * @param content The composable content to which this theme should be applied.
 */
@Composable
fun GospelACasoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
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
