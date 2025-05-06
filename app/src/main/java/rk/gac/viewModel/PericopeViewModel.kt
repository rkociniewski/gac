package rk.gac.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import rk.gac.data.PericopeProvider
import rk.gac.model.Config
import rk.gac.model.Pericope

class PericopeViewModel(application: Application) : AndroidViewModel(application) {

    private val provider = PericopeProvider(application)

    private val _config = MutableStateFlow(defaultConfig())
    val config: StateFlow<Config> = _config.asStateFlow()

    private val _pericopes = MutableStateFlow<List<Pericope>>(emptyList())
    val pericopes: StateFlow<List<Pericope>> = _pericopes.asStateFlow()

    private var lastSelectedIndex: Int? = null
    val selectedIndex: Int?
        get() = lastSelectedIndex

    fun updateConfig(newConfig: Config) {
        _config.value = newConfig
    }

    fun drawPericope() {
        viewModelScope.launch {
            val list = provider.getRandomPericopeWithContext(_config.value)
            _pericopes.value = list
            lastSelectedIndex = list.indexOfFirst { it.id == list.getOrNull(list.size / 2)?.id }
        }
    }

    private fun defaultConfig() = Config()
}
