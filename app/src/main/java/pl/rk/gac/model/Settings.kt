package pl.rk.gac.model

import pl.rk.gac.enums.AdditionalMode
import pl.rk.gac.enums.DisplayMode
import pl.rk.gac.enums.DrawMode
import pl.rk.gac.enums.Language
import pl.rk.gac.util.Numbers
import java.util.Locale

/**
 * Configuration data class for the application settings.
 *
 * This class stores are all user-configurable settings that control the application's behavior,
 * particularly regarding the display and drawing of pericopes (gospel sections).
 *
 * @property additionalMode Determines whether additional pericopes should be shown
 * @property wordThreshold Word count threshold used when additionalMode is set to CONDITIONAL
 * @property prevCount Number of previous pericopes to display before the current one
 * @property nextCount Number of next pericopes to display after the current one
 * @property startFallback Number of fallback pericopes to show at the beginning of a Gospel
 * @property endFallback Number of fallback pericopes to show at the end of a Gospel
 * @property displayMode Controls the application's theme (light, dark, or system)
 * @property drawMode Determines how drawing interactions are triggered (button, rotation, or both)
 */
data class Settings(
    val additionalMode: AdditionalMode = AdditionalMode.NO, // Mode to drawn additional pericopes
    val wordThreshold: Int = Numbers.WORD_THRESHOLD,        // only when CONDITIONAL
    val prevCount: Int = 0,                                 // how many pericopes before drawn
    val nextCount: Int = 1,                                 // how many pericopes after drawn
    val startFallback: Int = 1,                             // if begin of Gospel
    val endFallback: Int = 1,                               // if end of Evangelion 残酷な天使のように
    val displayMode: DisplayMode = DisplayMode.LIGHT,       // Display mMode for Dark Mode
    val drawMode: DrawMode = DrawMode.BUTTON,               // Draw mode
    val language: Language = resolveDefaultLanguage(),
)

fun resolveDefaultLanguage(): Language {
    val systemLangCode = Locale.getDefault().language.uppercase()
    return Language.entries.firstOrNull { it.name == systemLangCode } ?: Language.EN
}
