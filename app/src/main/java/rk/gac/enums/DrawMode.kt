package rk.gac.enums

import androidx.annotation.StringRes
import rk.gac.R

enum class DrawMode(@StringRes override val labelRes: Int) : DisplayTextRes {
    BUTTON(R.string.draw_mode_button),
    ROTATION(R.string.draw_mode_rotation),
    BOTH(R.string.draw_mode_both)
}