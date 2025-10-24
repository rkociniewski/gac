package rk.powermilk.gac.enums

import androidx.annotation.StringRes
import rk.powermilk.gac.R

/**
 * Enum representing additional mode options for the application.
 *
 * This enum defines possible states for additional functionality:
 * - YES: Additional functionality is always enabled
 * - NO: Additional functionality is always disabled
 * - CONDITIONAL: Additional functionality is enabled under specific conditions
 *
 * @property label Resource ID for the display label of this mode
 */
enum class AdditionalMode(@param:StringRes override val label: Int) : DisplayText {
    YES(R.string.additional_mode_yes),
    NO(R.string.additional_mode_no),
    CONDITIONAL(R.string.additional_mode_conditional)
}
