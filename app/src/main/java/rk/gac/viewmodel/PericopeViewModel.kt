package rk.gac.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import rk.gac.data.PericopeProvider
import rk.gac.model.Config
import rk.gac.model.Pericope

class PericopeViewModel(application: Application) : AndroidViewModel(application) {

    private val provider = PericopeProvider(application)

    private val _config = MutableStateFlow(Config())
    val config: StateFlow<Config> = _config.asStateFlow()

    private val _pericopes = MutableStateFlow<List<Pericope>>(emptyList())
    val pericopes: StateFlow<List<Pericope>> = _pericopes.asStateFlow()

    private var lastSelectedIndex: Int? = null
    val selectedIndex: Int?
        get() = lastSelectedIndex

    private val _error = MutableSharedFlow<String>()
    val error: SharedFlow<String> = _error.asSharedFlow()

    fun updateConfig(newConfig: Config) {
        _config.value = newConfig
    }

    fun drawPericope() {
        viewModelScope.launch {
            if (!_config.value.isValid()) {
                _error.emit("Nieprawidłowa konfiguracja: przynajmniej jedna perykopa (przed lub po) musi być włączona.")
                return@launch
            }

            val list = provider.getRandomPericopeWithContext(_config.value)
            _pericopes.value = list
            lastSelectedIndex = list.indexOfFirst { it.id == list.getOrNull(list.size / 2)?.id }
        }
    }
}
