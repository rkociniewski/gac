package rk.gac.viewmodel

import android.app.Application
import android.content.Context
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

    private val allPericopes = mutableListOf<Pericope>()

    var selectedIndex = 0
        private set

    val error = MutableSharedFlow<String>()

    private fun loadPericopesFromRaw(context: Context): List<Pericope> {
        val inputStream: InputStream = context.resources.openRawResource(R.raw.pl_gospel)
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        return Json.decodeFromString<List<Pericope>>(jsonString)
    }

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

    fun drawPericope() {
        val all = allPericopes
        if (all.isEmpty()) return

        val selected = all.random()
        println("[DEBUG] Wylosowana perykopa: ${selected.id}")
        val index = all.indexOf(selected)
        val config = _config.value

        val startIndex = maxOf(0, index - config.prevCount)
        val endIndex = minOf(all.lastIndex, index + config.nextCount)

        val selection = when (config.additionalMode) {
            AdditionalMode.NO -> listOf(selected)
            AdditionalMode.YES -> all.subList(startIndex, endIndex + 1)
            AdditionalMode.CONDITIONAL -> {
                val wordCount = selected.text.split(" ").size
                if (wordCount < config.wordThreshold) {
                    all.subList(startIndex, endIndex + 1)
                } else listOf(selected)
            }
        }

        // handle fallback if too close to start/end
        val adjustedSelection = when {
            firstIndex(index) == 0 -> {
                val extra = config.startFallback
                all.subList(0, minOf(all.size, selection.size + extra))
            }

            lastIndex(index) == all.lastIndex -> {
                val extra = config.endFallback
                val start = maxOf(0, all.size - selection.size - extra)
                all.subList(start, all.size)
            }

            else -> selection
        }

        _pericopes.value = adjustedSelection
        selectedIndex = adjustedSelection.indexOf(selected)
    }

    fun updateConfig(newConfig: Config) {
        _config.value = newConfig
        viewModelScope.launch {
            ConfigStore.write(context, newConfig)
        }
    }

    // Extensions for clarity
    private fun firstIndex(ref: Int) = ref
    private fun lastIndex(ref: Int) = ref
}