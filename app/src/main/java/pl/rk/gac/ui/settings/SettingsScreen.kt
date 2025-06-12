package pl.rk.gac.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import pl.rk.gac.R
import pl.rk.gac.enums.AdditionalMode
import pl.rk.gac.enums.DisplayMode
import pl.rk.gac.enums.DrawMode
import pl.rk.gac.enums.Language
import pl.rk.gac.model.Settings
import pl.rk.gac.ui.helper.HelpLabel
import pl.rk.gac.ui.helper.LanguageSelector
import pl.rk.gac.ui.helper.ModeSelector
import pl.rk.gac.ui.helper.SettingsSlider
import pl.rk.gac.util.Dimensions
import pl.rk.gac.util.Numbers

/**
 * A composable function that displays the configuration section in a modal dialog.
 *
 * This component provides UI controls for all application settings including additional mode options,
 * display mode preferences, and draw mode settings.
 *
 * @param settings The current configuration being edited
 * @param updateSettings Callback to update the configuration when changes are made
 * @param onClose Callback to close the configuration dialog
 */
@Composable
fun SettingsScreen(
    settings: Settings,
    updateSettings: (Settings) -> Unit,
    onClose: () -> Unit
) {
    Column(
        Modifier.padding(Dimensions.dialogPadding),
        Arrangement.Center,
        Alignment.CenterHorizontally
    ) {
        HelpLabel(
            stringResource(R.string.settings_label_language),
            stringResource(R.string.tooltip_language)

        )
        LanguageSelector(Language.entries, settings.language) {
            updateSettings(settings.copy(language = it))
        }

        HelpLabel(
            stringResource(R.string.section_additional_mode),
            stringResource(R.string.tooltip_additional_mode)
        )
        ModeSelector(AdditionalMode.entries, settings.additionalMode) {
            updateSettings(settings.copy(additionalMode = it))
        }

        if (settings.additionalMode == AdditionalMode.CONDITIONAL) {
            HelpLabel(
                stringResource(R.string.label_word_threshold),
                stringResource(R.string.tooltip_word_threshold)
            )
            SettingsSlider(settings.wordThreshold, (Numbers.TWENTY..Numbers.HUNDRED step Numbers.TEN)) {
                updateSettings(settings.copy(wordThreshold = it))
            }
        }

        if (settings.additionalMode != AdditionalMode.NO) {
            HelpLabel(stringResource(R.string.label_prev), stringResource(R.string.tooltip_prev))
            SettingsSlider(settings.prevCount) { updateSettings(settings.copy(prevCount = it)) }
            HelpLabel(stringResource(R.string.label_next), stringResource(R.string.tooltip_next))
            SettingsSlider(settings.nextCount) { updateSettings(settings.copy(nextCount = it)) }
            HelpLabel(
                stringResource(R.string.label_start_fallback),
                stringResource(R.string.tooltip_start_fallback)
            )
            SettingsSlider(settings.startFallback) { updateSettings(settings.copy(startFallback = it)) }
            HelpLabel(
                stringResource(R.string.label_end_fallback),
                stringResource(R.string.tooltip_end_fallback)
            )
            SettingsSlider(settings.endFallback) { updateSettings(settings.copy(endFallback = it)) }
        }

        HorizontalDivider()

        // Dark mode display mode
        HelpLabel(
            stringResource(R.string.section_display_mode),
            stringResource(R.string.tooltip_display_mode)
        )
        ModeSelector(DisplayMode.entries, settings.displayMode) {
            updateSettings(settings.copy(displayMode = it))
        }

        HelpLabel(
            stringResource(R.string.section_draw_mode),
            stringResource(R.string.tooltip_draw_mode)
        )
        ModeSelector(DrawMode.entries, settings.drawMode) {
            updateSettings(settings.copy(drawMode = it))
        }

        Spacer(modifier = Modifier.height(Dimensions.height))

        Button(
            onClick = onClose,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Dimensions.height)
        ) {
            Text(stringResource(R.string.close))
        }
    }
}

