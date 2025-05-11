package rk.gac

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import rk.gac.enums.DisplayMode
import rk.gac.ui.PericopeScreen
import rk.gac.ui.theme.GospelACasoTheme
import rk.gac.viewmodel.PericopeViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: PericopeViewModel = viewModel()
            val config by viewModel.config.collectAsState()

            val darkTheme = when (config.displayMode) {
                DisplayMode.SYSTEM -> isSystemInDarkTheme()
                DisplayMode.DARK -> true
                DisplayMode.LIGHT -> false
            }

            GospelACasoTheme(darkTheme) {
                PericopeScreen(viewModel)
            }
        }
    }
}
