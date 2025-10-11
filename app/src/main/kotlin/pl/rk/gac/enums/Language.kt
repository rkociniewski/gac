package pl.rk.gac.enums

import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.annotation.StringRes
import pl.rk.gac.R

/**
 * @property label Resource ID for the display label of this mode
 */
enum class Language(
    @param:StringRes override val label: Int,
    @param:DrawableRes val flagIcon: Int,
    @param:RawRes val resource: Int
) : DisplayText {
    PL(R.string.lang_pl, R.drawable.flag_pl, R.raw.pl_gospel),
    EN(R.string.lang_en, R.drawable.flag_uk, R.raw.en_gospel),
}
