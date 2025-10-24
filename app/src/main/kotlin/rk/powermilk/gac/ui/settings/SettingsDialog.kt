package rk.powermilk.gac.ui.settings

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import rk.powermilk.gac.R
import rk.powermilk.gac.enums.AdditionalMode
import rk.powermilk.gac.model.Settings
import rk.powermilk.gac.util.Dimensions

/**
 * Configuration dialog for application settings.
 *
 * This composable displays a modal dialog containing configuration options for the application.
 * It shows the configuration section and any validation error messages.
 *
 * @param settings The original configuration before any changes
 * @param onSettingsUpdate Callback to update the configuration when changes are made
 * @param onDismiss Callback invoked when the dialog is dismissed
 */
@Composable
fun SettingsDialog(
    settings: Settings,
    onSettingsUpdate: (Settings) -> Unit,
    onDismiss: () -> Unit,
    localizedContext: Context,
) {
    Dialog(onDismiss, DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(
            Modifier
                .fillMaxWidth(Dimensions.SCREEN_MARGIN)
                .padding(Dimensions.dialogPadding),
            MaterialTheme.shapes.medium,
            tonalElevation = Dimensions.height
        ) {
            Column(
                Modifier
                    .padding(Dimensions.dialogPadding)
                    .verticalScroll(rememberScrollState()),
                Arrangement.spacedBy(Dimensions.itemSpacing)
            ) {
                CompositionLocalProvider(LocalContext provides localizedContext) {
                    SettingsScreen(settings, onSettingsUpdate, onDismiss)

                    if (settings.additionalMode != AdditionalMode.NO &&
                        settings.prevCount == 0 && settings.nextCount == 0
                    ) {
                        SettingsErrorMessage()
                    }
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
private fun SettingsErrorMessage() {
    Text(
        text = stringResource(R.string.error_no_additional),
        color = MaterialTheme.colorScheme.error,
        style = MaterialTheme.typography.bodyMedium
    )
}
