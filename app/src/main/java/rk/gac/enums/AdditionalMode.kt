package rk.gac.enums

import androidx.annotation.StringRes
import rk.gac.R

enum class AdditionalMode(@StringRes override val labelRes: Int) : DisplayTextRes {
    YES(R.string.additional_mode_yes),
    NO(R.string.additional_mode_no),
    CONDITIONAL(R.string.additional_mode_conditional)
}
