package pl.rk.gac.storage

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import pl.rk.gac.enums.AdditionalMode
import pl.rk.gac.enums.DisplayMode
import pl.rk.gac.enums.DrawMode
import pl.rk.gac.model.Config

/**
 * Manages configuration data persistence using Jetpack DataStore preferences.
 * Provides functionality to read and write application configuration settings.
 */
private val Context.dataStore by preferencesDataStore("config")

/**
 * Object responsible for managing application configuration data.
 * Uses Android's DataStore preferences for persisting configuration values.
 */
object ConfigStore {
    private val ADDITIONAL_MODE = stringPreferencesKey("additional_mode")
    private val WORD_THRESHOLD = intPreferencesKey("word_threshold")
    private val PREV_COUNT = intPreferencesKey("prev_count")
    private val NEXT_COUNT = intPreferencesKey("next_count")
    private val START_FALLBACK = intPreferencesKey("start_fallback")
    private val END_FALLBACK = intPreferencesKey("end_fallback")
    private val DISPLAY_MODE = stringPreferencesKey("display_mode")
    private val DRAW_MODE = stringPreferencesKey("draw_mode")

    fun read(context: Context): Flow<Config> {
        return context.dataStore.data.map { prefs ->
            Config(
                additionalMode = AdditionalMode.valueOf(prefs[ADDITIONAL_MODE] ?: AdditionalMode.NO.name),
                wordThreshold = prefs[WORD_THRESHOLD] ?: 50,
                prevCount = prefs[PREV_COUNT] ?: 1,
                nextCount = prefs[NEXT_COUNT] ?: 1,
                startFallback = prefs[START_FALLBACK] ?: 1,
                endFallback = prefs[END_FALLBACK] ?: 1,
                displayMode = DisplayMode.valueOf(prefs[DISPLAY_MODE] ?: DisplayMode.LIGHT.name),
                drawMode = DrawMode.valueOf(prefs[DRAW_MODE] ?: DrawMode.BUTTON.name)
            )
        }
    }

    suspend fun write(context: Context, config: Config) {
        context.dataStore.edit { prefs ->
            prefs[ADDITIONAL_MODE] = config.additionalMode.name
            prefs[WORD_THRESHOLD] = config.wordThreshold
            prefs[PREV_COUNT] = config.prevCount
            prefs[NEXT_COUNT] = config.nextCount
            prefs[START_FALLBACK] = config.startFallback
            prefs[END_FALLBACK] = config.endFallback
            prefs[DISPLAY_MODE] = config.displayMode.name
            prefs[DRAW_MODE] = config.drawMode.name
        }
    }
}
