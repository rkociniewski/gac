package rk.gac.enums

import androidx.annotation.StringRes
import rk.gac.R

enum class DisplayMode(@StringRes override val labelRes: Int) : DisplayTextRes {
    LIGHT(R.string.display_mode_light),
    DARK(R.string.display_mode_dark),
    SYSTEM(R.string.display_mode_system)
}