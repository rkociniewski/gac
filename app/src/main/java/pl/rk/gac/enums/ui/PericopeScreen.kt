package pl.rk.gac.enums.ui

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Shuffle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import pl.rk.gac.enums.enums.DrawMode
import pl.rk.gac.enums.model.Config
import pl.rk.gac.enums.model.Pericope
import pl.rk.gac.enums.ui.config.ConfigSection
import rk.gac.R
import rk.gac.enums.AdditionalMode
import pl.rk.gac.enums.viewmodel.PericopeViewModel

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
fun PericopeScreen(viewModel: PericopeViewModel) {
    val context = LocalContext.current
    val initialConfig by viewModel.config.collectAsState()
    var currentConfig by remember { mutableStateOf(initialConfig) }
    val pericopes by viewModel.pericopes.collectAsState()
    val selectedId by viewModel.selectedId.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var showConfigDialog by remember { mutableStateOf(false) }

    HandleOrientationChange(viewModel, currentConfig)
    InitialSetupAndErrorHandling(viewModel, snackBarHostState)

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) },
        topBar = {
            PericopeTopAppBar(
                currentConfig = currentConfig,
                onDrawClick = {
                    handleDrawClick(context, currentConfig, viewModel, scope, snackBarHostState)
                },
                onConfigClick = { showConfigDialog = true }
            )
        }
    ) { padding ->
        PericopeContent(
            pericopes = pericopes,
            selectedId = selectedId,
            modifier = Modifier.padding(padding)
        )
    }

    if (showConfigDialog) {
        ConfigDialog(
            initialConfig = initialConfig,
            currentConfig = currentConfig,
            onConfigUpdate = { newConfig ->
                currentConfig = newConfig
                viewModel.updateConfig(newConfig)
            },
            onDismiss = {
                showConfigDialog = false
                viewModel.updateConfig(currentConfig)
                viewModel.drawPericope()
            }
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
 * @param currentConfig The current application configuration
 */
@Composable
private fun HandleOrientationChange(viewModel: PericopeViewModel, currentConfig: Config) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    var lastOrientation by remember { mutableIntStateOf(configuration.orientation) }

    LaunchedEffect(configuration.orientation) {
        Log.d(
            "rk.gac",
            "[DEBUG] ${
                context.getString(
                    R.string.debug_drawn_changed_orientation,
                    configuration.orientation,
                    lastOrientation
                )
            }"
        )
        if ((currentConfig.drawMode == DrawMode.ROTATION || currentConfig.drawMode == DrawMode.BOTH) &&
            lastOrientation != configuration.orientation
        ) {
            Log.d(
                "rk.gac",
                "[DEBUG] ${context.getString(R.string.debug_drawn_pericope_orientation)}"
            )
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
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        Log.d("rk.gac", "[DEBUG] ${context.getString(R.string.debug_initial_drawn_pericope)}")
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
 * @param currentConfig The current application configuration
 * @param viewModel The ViewModel that handles pericope drawing
 * @param scope The coroutine scope for launching snackbar displays
 * @param snackBarHostState The host state for displaying snackbar messages
 */
private fun handleDrawClick(
    context: Context,
    currentConfig: Config,
    viewModel: PericopeViewModel,
    scope: CoroutineScope,
    snackBarHostState: SnackbarHostState
) {
    Log.d("rk.gac", "[DEBUG] ${context.getString(R.string.debug_shuffle_clicked)}")

    if (currentConfig.additionalMode != AdditionalMode.NO &&
        currentConfig.prevCount == 0 && currentConfig.nextCount == 0
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

/**
 * Top app bar for the pericope screen.
 *
 * This composable creates the application's top bar, which includes the app title and
 * action buttons for drawing new pericopes and accessing configuration options.
 *
 * @param currentConfig The current application configuration
 * @param onDrawClick Callback invoked when the draw button is clicked
 * @param onConfigClick Callback invoked when the configuration button is clicked
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PericopeTopAppBar(
    currentConfig: Config,
    onDrawClick: () -> Unit,
    onConfigClick: () -> Unit
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary
        ),
        title = { Text(stringResource(R.string.app_name)) },
        actions = {
            if (currentConfig.drawMode == DrawMode.BUTTON || currentConfig.drawMode == DrawMode.BOTH) {
                IconButton(onClick = onDrawClick) {
                    Icon(
                        Icons.Outlined.Shuffle,
                        contentDescription = stringResource(R.string.draw),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            IconButton(onClick = onConfigClick) {
                Icon(
                    Icons.Outlined.Settings,
                    contentDescription = stringResource(R.string.draw),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    )
}

/**
 * Main content area displaying the list of pericopes.
 *
 * This composable creates a scrollable column of pericope items, with the selected
 * pericope visually distinguished from others.
 *
 * @param pericopes List of pericopes to display
 * @param selectedId ID of the currently selected pericope
 * @param modifier Modifier to be applied to the content container
 */
@Composable
private fun PericopeContent(
    pericopes: List<Pericope>,
    selectedId: String?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()), Arrangement.spacedBy(12.dp)
    ) {
        pericopes.forEach { pericope ->
            PericopeItem(pericope, pericope.id == selectedId)
            Spacer(Modifier.height(12.dp))
        }
    }
}

/**
 * Individual pericope item display.
 *
 * This composable displays a single pericope with its reference, title, and text content.
 * The main (selected) pericope is displayed with bold text for emphasis.
 *
 * @param pericope The pericope data to display
 * @param isMain Whether this is the main (selected) pericope
 */
@Composable
private fun PericopeItem(pericope: Pericope, isMain: Boolean) {
    Text(
        text = "${pericope.reference} — ${pericope.title}\n${pericope.text}",
        fontWeight = if (isMain) FontWeight.Bold else FontWeight.Normal,
        style = MaterialTheme.typography.bodyLarge
    )
}

/**
 * Configuration dialog for application settings.
 *
 * This composable displays a modal dialog containing configuration options for the application.
 * It shows the configuration section and any validation error messages.
 *
 * @param initialConfig The original configuration before any changes
 * @param currentConfig The current working configuration being edited
 * @param onConfigUpdate Callback to update the configuration when changes are made
 * @param onDismiss Callback invoked when the dialog is dismissed
 */
@Composable
private fun ConfigDialog(
    initialConfig: Config,
    currentConfig: Config,
    onConfigUpdate: (Config) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ConfigSection(
                    config = initialConfig,
                    updateConfig = onConfigUpdate,
                    onClose = onDismiss
                )

                if (currentConfig.additionalMode != AdditionalMode.NO &&
                    currentConfig.prevCount == 0 && currentConfig.nextCount == 0
                ) {
                    ConfigErrorMessage()
                }
            }
        }
    }
}

/**
 * Error message displayed in the configuration dialog when invalid settings are detected.
 *
 * This composable displays an error message in the configuration dialog when the user
 * has selected settings that would result in no pericopes being displayed.
 */
@Composable
private fun ConfigErrorMessage() {
    Text(
        text = stringResource(R.string.error_no_additional),
        color = MaterialTheme.colorScheme.error,
        style = MaterialTheme.typography.bodyMedium
    )
}
