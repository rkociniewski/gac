package rk.powermilk.gac.ui.pericope

import android.app.Activity
import android.content.Context
import android.view.WindowManager
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.platform.LocalView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import rk.powermilk.gac.R
import rk.powermilk.gac.enums.AdditionalMode
import rk.powermilk.gac.enums.DrawMode
import rk.powermilk.gac.model.Settings
import rk.powermilk.gac.ui.settings.SettingsDialog
import rk.powermilk.gac.util.AppLogger
import rk.powermilk.gac.util.LogTags
import rk.powermilk.gac.viewmodel.PericopeViewModel

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
@Composable
fun PericopeScreen(
    viewModel: PericopeViewModel,
    localizedContext: Context
) {
    val settings by viewModel.settings.collectAsState()
    val pericopes by viewModel.pericopes.collectAsState()
    val selectedId by viewModel.selectedId.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var showSettingsDialog by remember { mutableStateOf(false) }
    val view = LocalView.current

    DisposableEffect(Unit) {
        val window = (view.context as? Activity)?.window
        window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        onDispose {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    HandleOrientationChange(viewModel, settings)
    InitialSetupAndErrorHandling(viewModel, snackBarHostState)

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) },
        topBar = {
            PericopeTopAppBar(
                settings = settings,
                onDrawClick = {
                    handleDrawClick(localizedContext, settings, viewModel, scope, snackBarHostState)
                },
                onSettingsClick = { showSettingsDialog = true }
            )
        }
    ) {
        PericopeContent(pericopes, selectedId, Modifier.padding(it), settings, viewModel::updateSettings)
    }

    if (showSettingsDialog) {
        SettingsDialog(
            settings, {
                viewModel.updateSettings(it)
            }, {
                //
                showSettingsDialog = false
                viewModel.updateSettings(settings)
                viewModel.drawPericope()
            }, localizedContext
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

        logger.debug("Orientation has changed. Previous: $lastOrientation. Actual: ${configuration.orientation}")
        if ((settings.drawMode == DrawMode.ROTATION || settings.drawMode == DrawMode.BOTH) &&
            lastOrientation != configuration.orientation
        ) {
            logger.debug("Executing drawPeriscope after change orientation")
            viewModel.drawPericope()
        }
        lastOrientation = configuration.orientation
    }
}

/**
 * Handles initial setup and error collection for the pericope screen.
 *
 * This composable performs the initial pericope draw when the screen launches and
 * sets up an error collection from the ViewModel to display in snackbars.
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
 * If the configuration results in no pericopes being displayed, it shows an error message.
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
