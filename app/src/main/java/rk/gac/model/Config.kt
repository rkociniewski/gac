package rk.gac.model

import rk.gac.enums.AdditionalMode
import rk.gac.enums.DisplayMode
import rk.gac.enums.DrawMode

data class Config(
    val additionalMode: AdditionalMode = AdditionalMode.NO, // Mode to drawn additional pericopes
    val wordThreshold: Int = 50,                            // only when CONDITIONAL
    val prevCount: Int = 0,                                 // how many pericopes before drawn
    val nextCount: Int = 1,                                 // how many pericopes after drawn
    val startFallback: Int = 1,                             // if begin of Gospel
    val endFallback: Int = 1,                               // if end of Evangelion 残酷な天使のように
    val displayMode: DisplayMode = DisplayMode.LIGHT,       // DisplayMode for Dark Mode
    val drawMode: DrawMode = DrawMode.BUTTON                // DisplayMode for Dark Mode
)