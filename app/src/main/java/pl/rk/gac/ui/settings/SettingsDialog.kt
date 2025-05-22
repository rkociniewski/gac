package pl.rk.gac.ui.settings

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import pl.rk.gac.R
import pl.rk.gac.enums.AdditionalMode
import pl.rk.gac.model.Settings
import pl.rk.gac.util.Dimensions

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
) {
    Dialog(onDismiss, DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(
            Modifier
                .fillMaxWidth(Dimensions.SCREEN_MARGI)
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
