package pl.rk.gac.enums

import androidx.annotation.StringRes
import pl.rk.gac.R

/**
 * Enum representing drawing interaction modes in the application.
 *
 * This enum defines possible methods for interacting with drawable elements:
 * - BUTTON: Drawing is triggered through button press
 * - ROTATION: Drawing is triggered through device rotation
 * - BOTH: Drawing can be triggered through both button press and device rotation
 *
 * @property label Resource ID for the display label of this mode
 */
enum class DrawMode(@param:StringRes override val label: Int) : DisplayText {
    BUTTON(R.string.draw_mode_button),
    ROTATION(R.string.draw_mode_rotation),
    BOTH(R.string.draw_mode_both)
}
