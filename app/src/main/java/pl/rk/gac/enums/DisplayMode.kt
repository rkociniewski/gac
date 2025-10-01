package pl.rk.gac.enums

import androidx.annotation.StringRes
import pl.rk.gac.R

/**
 * Enum representing display theme modes for the application.
 *
 * This enum defines possible theme options for the application's user interface:
 * - LIGHT: Forces light theme regardless of system settings
 * - DARK: Forces dark theme regardless of system settings
 * - SYSTEM: Uses the device's system theme settings
 *
 * @property label Resource ID for the display label of this mode
 */
enum class DisplayMode(@param:StringRes override val label: Int) : DisplayText {
    LIGHT(R.string.display_mode_light),
    DARK(R.string.display_mode_dark),
    SYSTEM(R.string.display_mode_system)
}
