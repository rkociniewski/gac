package rk.gac.ui

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
import kotlinx.coroutines.launch
import rk.gac.R
import rk.gac.enums.AdditionalMode
import rk.gac.enums.DrawMode
import rk.gac.ui.config.ConfigSection
import rk.gac.viewmodel.PericopeViewModel

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

@Composable
private fun HandleOrientationChange(viewModel: PericopeViewModel, currentConfig: rk.gac.model.Config) {
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

private fun handleDrawClick(
    context: android.content.Context,
    currentConfig: rk.gac.model.Config,
    viewModel: PericopeViewModel,
    scope: kotlinx.coroutines.CoroutineScope,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PericopeTopAppBar(
    currentConfig: rk.gac.model.Config,
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

@Composable
private fun PericopeContent(
    pericopes: List<rk.gac.model.Pericope>,
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

@Composable
private fun PericopeItem(pericope: rk.gac.model.Pericope, isMain: Boolean) {
    Text(
        text = "${pericope.reference} — ${pericope.title}\n${pericope.text}",
        fontWeight = if (isMain) FontWeight.Bold else FontWeight.Normal,
        style = MaterialTheme.typography.bodyLarge
    )
}

@Composable
private fun ConfigDialog(
    initialConfig: rk.gac.model.Config,
    currentConfig: rk.gac.model.Config,
    onConfigUpdate: (rk.gac.model.Config) -> Unit,
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

@Composable
private fun ConfigErrorMessage() {
    Text(
        text = stringResource(R.string.error_no_additional),
        color = MaterialTheme.colorScheme.error,
        style = MaterialTheme.typography.bodyMedium
    )
}
