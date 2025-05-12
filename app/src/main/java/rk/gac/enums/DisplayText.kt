package rk.gac.enums

import androidx.annotation.StringRes

/**
 * Interface for enums that have a displayable text resource.
 *
 * This interface ensures that implementing enums provide a string resource
 * that can be used for displaying their values in the user interface.
 *
 * @property label Resource ID for the display label text
 */
interface DisplayText {
    @get:StringRes
    val label: Int
}
