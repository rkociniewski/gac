package rk.gac.ui.config

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults.rememberPlainTooltipPositionProvider
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import rk.gac.R
import rk.gac.enums.AdditionalMode
import rk.gac.enums.DisplayMode
import rk.gac.enums.DisplayText
import rk.gac.enums.DrawMode
import rk.gac.model.Config
import kotlin.enums.EnumEntries

/**
 * A composable function that displays the configuration section in a modal dialog.
 *
 * This component provides UI controls for all application settings including additional mode options,
 * display mode preferences, and draw mode settings.
 *
 * @param config The current configuration being edited
 * @param updateConfig Callback to update the configuration when changes are made
 * @param onClose Callback to close the configuration dialog
 */
@Composable
fun ConfigSection(
    config: Config,
    updateConfig: (Config) -> Unit,
    onClose: () -> Unit
) {
    // Additional mode pericopes
    HelpLabel(
        stringResource(R.string.section_additional_mode),
        stringResource(R.string.tooltip_additional_mode)
    )
    ModeSelector(AdditionalMode.entries, config.additionalMode) {
        updateConfig(config.copy(additionalMode = it))
    }

    if (config.additionalMode == AdditionalMode.CONDITIONAL) {
        HelpLabel(
            stringResource(R.string.label_word_threshold),
            stringResource(R.string.tooltip_word_threshold)
        )
        @Suppress("MagicNumber")
        ConfigSlider(config.wordThreshold,  (20..100 step 10)) {
            updateConfig(config.copy(wordThreshold = it))
        }
    }

    if (config.additionalMode != AdditionalMode.NO) {
        HelpLabel(stringResource(R.string.label_prev), stringResource(R.string.tooltip_prev))
        ConfigSlider(config.prevCount) { updateConfig(config.copy(prevCount = it)) }
        HelpLabel(stringResource(R.string.label_next), stringResource(R.string.tooltip_next))
        ConfigSlider(config.nextCount) { updateConfig(config.copy(nextCount = it)) }
        HelpLabel(
            stringResource(R.string.label_start_fallback),
            stringResource(R.string.tooltip_start_fallback)
        )
        ConfigSlider(config.startFallback) { updateConfig(config.copy(startFallback = it)) }
        HelpLabel(
            stringResource(R.string.label_end_fallback),
            stringResource(R.string.tooltip_end_fallback)
        )
        ConfigSlider(config.endFallback) { updateConfig(config.copy(endFallback = it)) }
    }

    HorizontalDivider()

    // Dark mode display mode
    HelpLabel(
        stringResource(R.string.section_display_mode),
        stringResource(R.string.tooltip_display_mode)
    )
    ModeSelector(DisplayMode.entries, config.displayMode) {
        updateConfig(config.copy(displayMode = it))
    }

    HelpLabel(
        stringResource(R.string.section_draw_mode),
        stringResource(R.string.tooltip_draw_mode)
    )
    ModeSelector(DrawMode.entries, config.drawMode) {
        updateConfig(config.copy(drawMode = it))
    }

    Spacer(modifier = Modifier.height(8.dp))

    Button(
        onClick = onClose,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    ) {
        Text(stringResource(R.string.close))
    }
}

/**
 * A composable function that displays a label with an information tooltip.
 *
 * This component pairs a text label with an information icon. When the icon is clicked,
 * it displays a tooltip with additional helpful information.
 *
 * @param label The primary text to display as the label
 * @param tooltip The text to display in the tooltip when the info icon is clicked
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpLabel(label: String, tooltip: String) {
    val tooltipState = rememberTooltipState()
    val scope = rememberCoroutineScope()

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(label, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.width(4.dp))
        TooltipBox(
            positionProvider = rememberPlainTooltipPositionProvider(),
            tooltip = {
                PlainTooltip {
                    Text(tooltip)
                }
            },
            state = tooltipState,
        ) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = stringResource(R.string.tooltip_icon_description),
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(4.dp)
                    .clickable {
                        scope.launch {
                            if (!tooltipState.isVisible) {
                                tooltipState.show()
                            } else {
                                tooltipState.dismiss()
                            }
                        }
                    }
            )
        }
    }
}

/**
 * A composable function that creates a row of selectable filter chips for enum options.
 *
 * This generic component creates a horizontal row of filter chips based on the provided enum options.
 * It highlights the currently selected option and invokes the onSelect callback when a different option is chosen.
 *
 * @param T The enum type that implements DisplayText interface
 * @param options The enum entries to display as options
 * @param selected The currently selected enum value
 * @param onSelect Callback invoked when a different option is selected
 */
@Composable
fun <T> ModeSelector(
    options: EnumEntries<T>,
    selected: T,
    onSelect: (T) -> Unit
) where T : Enum<T>, T : DisplayText {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEach { opt ->
            FilterChip(
                selected = selected == opt,
                onClick = { onSelect(opt) },
                label = { Text(stringResource(opt.label)) }
            )
        }
    }
}

/**
 * A composable function that displays a slider with a text value indicator.
 *
 * This component provides a slider control for numerical values with a text display
 * of the current value centered beneath the slider.
 *
 * @param value The current integer value of the slider
 * @param range The range of possible values for the slider with a specified step
 * @param onValueChange Callback invoked when the slider value changes
 */
@Composable
fun ConfigSlider(value: Int, range: IntProgression = 0..2 step 1, onValueChange: (Int) -> Unit){
    Column {
        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.toInt()) },
            valueRange = range.first.toFloat()..range.last.toFloat(),
            steps = ((range.last - range.first) / range.step) - 1
        )
        Text("$value", modifier = Modifier.align(Alignment.CenterHorizontally))
    }
}
