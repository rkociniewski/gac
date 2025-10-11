package pl.rk.gac.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.rk.gac.di.SettingsRepository
import pl.rk.gac.enums.AdditionalMode
import pl.rk.gac.model.Pericope
import pl.rk.gac.model.Settings
import pl.rk.gac.util.AppLogger
import pl.rk.gac.util.LogTags

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
 */
@HiltViewModel
class PericopeViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    private val logger = AppLogger(this::class.simpleName ?: LogTags.PERICOPE_VIEW_MODEL)

    /** Mutable state flow for the current configuration */
    private val _settings = MutableStateFlow(Settings())

    /** Public state flow exposing the current configuration */
    val settings: StateFlow<Settings> = _settings

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
     * Checks if a pericope belongs to a specific gospel.
     *
     * @param periscope The pericope to check
     * @param gospel The gospel prefix (e.g., "mt", "mk")
     * @return True if the pericope belongs to the specified gospel, false otherwise
     */
    private fun isSameGospel(periscope: Pericope?, gospel: String) = periscope?.id?.startsWith("${gospel}_") == true

    /**
     * Initializes the ViewModel by loading the configuration and pericopes.
     */
    init {
        viewModelScope.launch {
            settingsRepository.settingsFlow.collect {
                val shouldReload = _settings.value.language != it.language
                _settings.value = it

                if (shouldReload || allPericopes.isEmpty()) {
                    val list = settingsRepository.loadPericopesFromRaw(it.language)
                    allPericopes.clear()
                    allPericopes.addAll(list)
                    drawPericope()
                }
            }
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

        logger.debug("Drawn pericope: ${selectedPericope.id}")

        val settings = _settings.value
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
            selectedPericope.text.trim().split("\\s+".toRegex()).size <= settings.wordThreshold

        val useAdditional = when (settings.additionalMode) {
            AdditionalMode.YES -> true
            AdditionalMode.NO -> false
            AdditionalMode.CONDITIONAL -> thresholdMet
        }

        val prevCount = when {
            useAdditional && isAtEnd -> settings.endFallback
            useAdditional && isAtStart -> 0
            useAdditional -> settings.prevCount
            else -> 0
        }

        val nextCount = when {
            useAdditional && isAtStart -> settings.startFallback
            useAdditional && isAtEnd -> 0
            useAdditional -> settings.nextCount
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
     * @param settings The new configuration to apply
     */
    fun updateSettings(settings: Settings) {
        viewModelScope.launch {
            val writeSuccess = settingsRepository.updateSettings(settings)

            if (writeSuccess) {
                _settings.value = settings

                logger.debug("Settings updated: $settings")
            } else {
                logger.error("Failed to persist settings")
            }
        }
    }
}
