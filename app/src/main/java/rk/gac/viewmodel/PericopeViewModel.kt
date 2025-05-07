package rk.gac.viewmodel

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
import rk.gac.R
import rk.gac.enums.AdditionalMode
import rk.gac.model.Config
import rk.gac.model.Pericope
import rk.gac.storage.ConfigStore
import java.io.InputStream

class PericopeViewModel(app: Application) : AndroidViewModel(app) {
    private val context: Context get() = getApplication()

    private val _config = MutableStateFlow(Config())
    val config: StateFlow<Config> = _config

    private val _pericopes = MutableStateFlow<List<Pericope>>(emptyList())
    val pericopes: StateFlow<List<Pericope>> = _pericopes

    internal val allPericopes = mutableListOf<Pericope>()

    var selectedIndex = 0
        private set

    val error = MutableSharedFlow<String>()

    private fun loadPericopesFromRaw(context: Context): List<Pericope> {
        val inputStream: InputStream = context.resources.openRawResource(R.raw.pl_gospel)
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        return Json.decodeFromString<List<Pericope>>(jsonString)
    }

    private fun isSameGospel(p: Pericope?, gp: String): Boolean =
        p?.id?.startsWith("${gp}_") ?: false

    init {
        viewModelScope.launch {
            // load persisted config
            ConfigStore.read(context).collect { storedConfig ->
                _config.value = storedConfig
            }
        }
        viewModelScope.launch {
            val list = loadPericopesFromRaw(context)
            allPericopes.clear()
            allPericopes.addAll(list)
            drawPericope()
        }
    }

    fun drawPericope(forcedIndex: Int? = null) {
        if (allPericopes.isEmpty()) return

        val selected = forcedIndex ?: allPericopes.indices.random()
        val selectedPericope = allPericopes[selected]
        selectedIndex = 0 // will be recalculated below

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

        val thresholdMet = selectedPericope.text.trim().split("\\s+").size <= config.wordThreshold

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

        selectedIndex = result.indexOfFirst { it.id == selectedPericope.id }
        _pericopes.value = result
    }

    fun updateConfig(newConfig: Config) {
        _config.value = newConfig
        viewModelScope.launch {
            ConfigStore.write(context, newConfig)
        }
    }
}