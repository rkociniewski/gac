package pl.rk.gac.ui.pericope

import android.content.Context
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import pl.rk.gac.R
import pl.rk.gac.enums.AdditionalMode
import pl.rk.gac.enums.DrawMode
import pl.rk.gac.model.Settings
import pl.rk.gac.ui.settings.SettingsDialog
import pl.rk.gac.util.AppLogger
import pl.rk.gac.util.LogTags
import pl.rk.gac.viewmodel.PericopeViewModel

private val logger = AppLogger(LogTags.PERICOPE_SCREEN)

/**
 * Main screen composable for displaying pericopes (Gospel passages).
 *
 * This component serves as the main UI for the application, integrating all the necessary
 * functionality for viewing Gospel passages, handling configuration changes, and managing
 * orientation-based interactions.
 *
 * The screen includes:
 * - A top app bar with draw and configuration options
 * - A scrollable list of pericope items
 * - A configuration dialog triggered by the settings button
 * - Orientation change handling for rotation-based draws
 * - Error handling and snackbar notifications
 *
 * @param viewModel The ViewModel that provides data and handles business logic for this screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PericopeScreen(
    viewModel: PericopeViewModel,
    localizedContext: Context
) {
    val context = LocalContext.current
    val settings by viewModel.settings.collectAsState()
    val pericopes by viewModel.pericopes.collectAsState()
    val selectedId by viewModel.selectedId.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var showSettingsDialog by remember { mutableStateOf(false) }

    HandleOrientationChange(viewModel, settings)
    InitialSetupAndErrorHandling(viewModel, snackBarHostState)

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) },
        topBar = {
            PericopeTopAppBar(
                settings = settings,
                onDrawClick = {
                    handleDrawClick(context, settings, viewModel, scope, snackBarHostState)
                },
                onSettingsClick = { showSettingsDialog = true }
            )
        }
    ) { padding ->
        PericopeContent(
            pericopes = pericopes,
            selectedId = selectedId,
            modifier = Modifier.padding(padding)
        )
    }

    if (showSettingsDialog) {
        SettingsDialog(
            settings = settings,
            onSettingsUpdate = {
                viewModel.updateSettings(it)
            },
            onDismiss = {
                showSettingsDialog = false
                viewModel.updateSettings(settings)
                viewModel.drawPericope()
            },
            localizedContext = localizedContext
        )
    }
}

/**
 * Handles device orientation changes for drawing pericopes.
 *
 * This composable monitors device orientation changes and triggers a pericope draw
 * when the configuration includes rotation-based drawing modes.
 *
 * @param viewModel The ViewModel that handles the drawing of pericopes
 * @param settings The current application configuration
 */
@Composable
private fun HandleOrientationChange(viewModel: PericopeViewModel, settings: Settings) {
    val configuration = LocalConfiguration.current
    var lastOrientation by remember { mutableIntStateOf(configuration.orientation) }

    LaunchedEffect(configuration.orientation) {

        logger.debug("Orientantion has changed. Previous: $lastOrientation. Actual: ${configuration.orientation}")
        if ((settings.drawMode == DrawMode.ROTATION || settings.drawMode == DrawMode.BOTH) &&
            lastOrientation != configuration.orientation
        ) {
            logger.debug("Executing drawPeriscope after change orientantion")
            viewModel.drawPericope()
        }
        lastOrientation = configuration.orientation
    }
}

/**
 * Handles initial setup and error collection for the pericope screen.
 *
 * This composable performs the initial pericope draw when the screen launches and
 * sets up error collection from the ViewModel to display in snackbars.
 *
 * @param viewModel The ViewModel that provides pericopes and error information
 * @param snackBarHostState The host state for displaying snackbar messages
 */
@Composable
private fun InitialSetupAndErrorHandling(
    viewModel: PericopeViewModel,
    snackBarHostState: SnackbarHostState
) {
    LaunchedEffect(Unit) {
        logger.debug("Initial pericope drawn")
        viewModel.drawPericope()

        viewModel.error.collect {
            snackBarHostState.showSnackbar(it)
        }
    }
}

/**
 * Handles the draw button click event.
 *
 * This function validates the current configuration before drawing a new pericope.
 * If the configuration would result in no pericopes being displayed, it shows an error message.
 *
 * @param context The Android context for resource access
 * @param currentSettings The current application configuration
 * @param viewModel The ViewModel that handles pericope drawing
 * @param scope The coroutine scope for launching snackbar displays
 * @param snackBarHostState The host state for displaying snackbar messages
 */
private fun handleDrawClick(
    context: Context,
    currentSettings: Settings,
    viewModel: PericopeViewModel,
    scope: CoroutineScope,
    snackBarHostState: SnackbarHostState
) {
    logger.debug("Shuffle Icon clicked")

    if (currentSettings.additionalMode != AdditionalMode.NO &&
        currentSettings.prevCount == 0 && currentSettings.nextCount == 0
    ) {
        scope.launch {
            snackBarHostState.showSnackbar(
                "${context.getString(R.string.debug_cant_drawn)}: ${
                    context.getString(R.string.error_no_additional)
                }"
            )
        }
    } else {
        viewModel.drawPericope()
    }
}
