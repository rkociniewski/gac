package pl.rk.gac.util

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import pl.rk.gac.enums.AdditionalMode
import pl.rk.gac.enums.DisplayMode
import pl.rk.gac.enums.DrawMode
import pl.rk.gac.model.Settings

/**
 * Manages configuration data persistence using Jetpack DataStore preferences.
 * Provides functionality to read and write application configuration settings.
 */
private val Context.dataStore by preferencesDataStore("settings")

/**
 * Object responsible for managing application configuration data.
 * Uses Android's DataStore preferences for persisting configuration values.
 */
object SettingsStore {
    private val logger = AppLogger(this::class.simpleName ?: LogTags.SETTINGS_STORE)

    private val ADDITIONAL_MODE = stringPreferencesKey("additional_mode")
    private val WORD_THRESHOLD = intPreferencesKey("word_threshold")
    private val PREV_COUNT = intPreferencesKey("prev_count")
    private val NEXT_COUNT = intPreferencesKey("next_count")
    private val START_FALLBACK = intPreferencesKey("start_fallback")
    private val END_FALLBACK = intPreferencesKey("end_fallback")
    private val DISPLAY_MODE = stringPreferencesKey("display_mode")
    private val DRAW_MODE = stringPreferencesKey("draw_mode")

    fun read(context: Context): Flow<Settings> {
        return context.dataStore.data.map {
            val settings = Settings(
                safeEnumValueOf(it[ADDITIONAL_MODE], AdditionalMode.NO),
                it[WORD_THRESHOLD] ?: Numbers.WORD_THRESHOLD,
                it[PREV_COUNT] ?: 1,
                it[NEXT_COUNT] ?: 1,
                it[START_FALLBACK] ?: 1,
                it[END_FALLBACK] ?: 1,
                safeEnumValueOf(it[DISPLAY_MODE], DisplayMode.LIGHT),
                safeEnumValueOf(it[DRAW_MODE], DrawMode.BUTTON),
            )
            logger.debug("Loaded settings: $settings")
            settings
        }.catch {
            logger.error("Error reading settings", it)
            emit(Settings())
        }
    }

    @Suppress("TooGenericExceptionCaught")
    suspend fun write(context: Context, settings: Settings) = try {
        context.dataStore.edit {
            it[ADDITIONAL_MODE] = settings.additionalMode.name
            it[WORD_THRESHOLD] = settings.wordThreshold
            it[PREV_COUNT] = settings.prevCount
            it[NEXT_COUNT] = settings.nextCount
            it[START_FALLBACK] = settings.startFallback
            it[END_FALLBACK] = settings.endFallback
            it[DISPLAY_MODE] = settings.displayMode.name
            it[DRAW_MODE] = settings.drawMode.name
        }
        true
    } catch (e: Exception) {
        logger.error("Error writing settings", e)
        false
    }
}

// Safe fallback for enums
private inline fun <reified T : Enum<T>> safeEnumValueOf(name: String?, default: T): T {
    return enumValues<T>().firstOrNull { it.name == name } ?: default
}
