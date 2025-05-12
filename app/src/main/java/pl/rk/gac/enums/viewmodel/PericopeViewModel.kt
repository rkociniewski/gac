package pl.rk.gac.enums.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import pl.rk.gac.enums.model.Config
import pl.rk.gac.enums.model.Pericope
import pl.rk.gac.enums.storage.ConfigStore
import rk.gac.R
import rk.gac.enums.AdditionalMode
import java.io.InputStream

/**
 * PericopeViewModel.kt
 *
 * ViewModel that manages the gospel pericopes data and application configuration.
 * Handles loading, filtering, and selecting pericope passages based on user preferences.
 */

/**
 * ViewModel for managing gospel pericopes and application configuration.
 * Responsible for loading pericopes from resources, selecting passages based on
 * configured rules, and maintaining application state.
 *
 * @param app The application instance
 */
class PericopeViewModel(app: Application) : AndroidViewModel(app) {
    /** Application context for resource access */
    private val context: Context get() = getApplication()

    /** Mutable state flow for the current configuration */
    private val _config = MutableStateFlow(Config())

    /** Public state flow exposing the current configuration */
    val config: StateFlow<Config> = _config

    /** Mutable state flow for the currently displayed pericopes */
    private val _pericopes = MutableStateFlow<List<Pericope>>(emptyList())

    /** Public state flow exposing the currently displayed pericopes */
    val pericopes: StateFlow<List<Pericope>> = _pericopes

    /** Collection of all available pericopes loaded from resources */
    internal val allPericopes = mutableListOf<Pericope>()

    /** Mutable state flow for the currently selected pericope ID */
    private val _selectedId = MutableStateFlow<String?>(null)

    /** Public state flow exposing the currently selected pericope ID */
    val selectedId: StateFlow<String?> = _selectedId

    /** Shared flow for error messages */
    val error = MutableSharedFlow<String>()

    /**
     * Loads pericopes from raw resources.
     *
     * @param context The application context
     * @return List of pericopes parsed from the JSON resource file
     */
    private fun loadPericopesFromRaw(context: Context): List<Pericope> {
        val inputStream: InputStream = context.resources.openRawResource(R.raw.pl_gospel)
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        return Json.decodeFromString<List<Pericope>>(jsonString)
    }

    /**
     * Checks if a pericope belongs to a specific gospel.
     *
     * @param p The pericope to check
     * @param gp The gospel prefix (e.g., "mt", "mk")
     * @return True if the pericope belongs to the specified gospel, false otherwise
     */
    private fun isSameGospel(p: Pericope?, gp: String): Boolean =
        p?.id?.startsWith("${gp}_") == true

    /**
     * Initializes the ViewModel by loading the configuration and pericopes.
     */
    init {
        viewModelScope.launch {
            // load persisted config
            ConfigStore.read(context).collect { _config.value = it }
        }

        viewModelScope.launch {
            val list = loadPericopesFromRaw(context)
            allPericopes.clear()
            allPericopes.addAll(list)
            drawPericope()
        }
    }

    /**
     * Selects a pericope and determines which adjacent pericopes to display based on configuration.
     * Handles special cases such as start/end of gospels and word count thresholds.
     *
     * @param forcedIndex Optional index to force selection of a specific pericope
     */
    fun drawPericope(forcedIndex: Int? = null) {
        if (allPericopes.isEmpty()) return

        val selected = forcedIndex ?: allPericopes.indices.random()
        val selectedPericope = allPericopes[selected]
        _selectedId.value = selectedPericope.id

        Log.d(
            "rk.gac",
            "[DEBUG] ${context.getString(R.string.debug_drawn_pericope, selectedPericope.id)}"
        )

        val config = _config.value
        val result = mutableListOf<Pericope>()

        // Determine the gospel prefix, e.g. "mt", "mk"
        val gospelPrefix = selectedPericope.id.takeWhile { it != '_' }

        // Determine if this is the first pericope of the gospel
        val isAtStart =
            selected == 0 || !isSameGospel(allPericopes.getOrNull(selected - 1), gospelPrefix)

        // Determine if this is the last pericope of the gospel
        val isAtEnd = selected == allPericopes.lastIndex || !isSameGospel(
            allPericopes.getOrNull(selected + 1),
            gospelPrefix
        )

        val thresholdMet =
            selectedPericope.text.trim().split("\\s+".toRegex()).size <= config.wordThreshold

        val useAdditional = when (config.additionalMode) {
            AdditionalMode.YES -> true
            AdditionalMode.NO -> false
            AdditionalMode.CONDITIONAL -> thresholdMet
        }

        val prevCount = when {
            useAdditional && isAtEnd -> config.endFallback
            useAdditional && isAtStart -> 0
            useAdditional -> config.prevCount
            else -> 0
        }

        val nextCount = when {
            useAdditional && isAtStart -> config.startFallback
            useAdditional && isAtEnd -> 0
            useAdditional -> config.nextCount
            else -> 0
        }

        val from = (selected - prevCount).coerceAtLeast(0)
        val to = (selected + nextCount).coerceAtMost(allPericopes.lastIndex)

        for (i in from..to) {
            result.add(allPericopes[i])
        }

        _pericopes.value = result
    }

    /**
     * Updates the application configuration and persists the changes.
     *
     * @param newConfig The new configuration to apply
     */
    fun updateConfig(newConfig: Config) {
        Log.d("rk.gac", "[DEBUG] ${context.getString(R.string.debug_config_updated, newConfig)}")

        _config.value = newConfig
        viewModelScope.launch {
            ConfigStore.write(context, newConfig)
        }
    }
}
