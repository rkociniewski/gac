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
    val config by viewModel.config.collectAsState()
    val pericopes by viewModel.pericopes.collectAsState()
    val selectedIndex = viewModel.selectedIndex
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var showConfigDialog by remember { mutableStateOf(false) }

    val configuration = LocalConfiguration.current
    var lastOrientation by remember { mutableIntStateOf(configuration.orientation) }

    // Handle orientation change
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
        if ((config.drawMode == DrawMode.ROTATION || config.drawMode == DrawMode.BOTH) &&
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

    // Initial draw and error handling
    LaunchedEffect(Unit) {
        Log.d("rk.gac", "[DEBUG] ${context.getString(R.string.debug_initial_drawn_pericope)}")
        viewModel.drawPericope()
        viewModel.error.collect { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    // Configuration modal dialog
    if (showConfigDialog) {
        Dialog(onDismissRequest = { showConfigDialog = false }) {
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
                        initialConfig = config,
                        onConfigChange = {
                            viewModel.updateConfig(it)
                            showConfigDialog = false
                        },
                    )
                    if (config.additionalMode != AdditionalMode.NO &&
                        config.prevCount == 0 && config.nextCount == 0
                    ) {
                        Text(
                            text = stringResource(R.string.error_no_additional),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                actions = {
                    if (config.drawMode == DrawMode.BUTTON || config.drawMode == DrawMode.BOTH) {
                        IconButton(onClick = {
                            Log.d(
                                "rk.gac",
                                "[DEBUG] ${context.getString(R.string.debug_shuffle_clicked)}"
                            )
                            if (config.additionalMode != AdditionalMode.NO &&
                                config.prevCount == 0 && config.nextCount == 0
                            ) {
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        "${context.getString(R.string.debug_cant_drawn)}: ${
                                            context.getString(
                                                R.string.error_no_additional
                                            )
                                        }"
                                    )
                                }
                            } else {
                                viewModel.drawPericope()
                            }
                        }) {
                            Icon(
                                Icons.Outlined.Shuffle,
                                contentDescription = stringResource(R.string.draw)
                            )
                        }
                    }
                    IconButton(onClick = { showConfigDialog = true }) {
                        Icon(
                            Icons.Outlined.Settings,
                            contentDescription = stringResource(R.string.settings)
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            pericopes.forEachIndexed { index, p ->
                Text(
                    text = "${p.reference} — ${p.title}\n${p.text}",
                    fontWeight = if (index == selectedIndex) FontWeight.Bold else FontWeight.Normal,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}
