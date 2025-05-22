package pl.rk.gac

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import pl.rk.gac.ui.pericope.PericopeScreen
import pl.rk.gac.ui.theme.GospelACasoTheme
import pl.rk.gac.ui.util.isDarkTheme
import pl.rk.gac.ui.util.rememberLocalizedContext
import pl.rk.gac.viewmodel.PericopeViewModel

/**
 * MainActivity.kt
 *
 * The application's main entry point activity.
 * Sets up the UI theme based on configuration and initializes the main pericope screen.
 */

/**
 * Main activity that initializes the application UI.
 * Sets up the theme based on user preferences and displays the pericope screen.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    /**
     * Sets up the activity with appropriate theme and content.
     *
     * @param savedInstanceState If the activity is being re-initialized, this Bundle
     * contains the data it most recently supplied in onSaveInstanceState.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val viewModel: PericopeViewModel = hiltViewModel()
            val settings by viewModel.settings.collectAsState()

            val darkTheme = settings.displayMode.isDarkTheme()

            val localizedContext = rememberLocalizedContext(settings.language.name.lowercase())
            CompositionLocalProvider(LocalContext provides localizedContext) {
                GospelACasoTheme(darkTheme) {
                    PericopeScreen(viewModel, localizedContext)
                }
            }
        }
    }
}
