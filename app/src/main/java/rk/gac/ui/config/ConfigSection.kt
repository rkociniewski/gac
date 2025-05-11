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
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import rk.gac.R
import rk.gac.enums.AdditionalMode
import rk.gac.enums.DisplayMode
import rk.gac.enums.DisplayTextRes
import rk.gac.enums.DrawMode
import rk.gac.model.Config
import kotlin.enums.EnumEntries

@Composable
fun ConfigSection(
    config: Config,
    updateConfig: (Config) -> Unit,
    onClose: () -> Unit
) {
    val view = LocalView.current

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
        WordThresholdSelector(config.wordThreshold) {
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


@Composable
fun <T> ModeSelector(
    options: EnumEntries<T>,
    selected: T,
    onSelect: (T) -> Unit
) where T : Enum<T>, T : DisplayTextRes {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEach { opt ->
            FilterChip(
                selected = selected == opt,
                onClick = { onSelect(opt) },
                label = { Text(stringResource(opt.labelRes)) }
            )
        }
    }
}

@Composable
fun WordThresholdSelector(selected: Int, onChange: (Int) -> Unit) {
    val options = listOf(25, 50, 75, 100)
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEach {
            FilterChip(
                selected = selected == it,
                onClick = { onChange(it) },
                label = { Text("$it") }
            )
        }
    }
}

@Composable
fun ConfigSlider(value: Int, onValueChange: (Int) -> Unit) {
    SliderWithLabel(value = value, onValueChange = onValueChange, range = 0..2)
}

@Composable
fun SliderWithLabel(value: Int, onValueChange: (Int) -> Unit, range: IntRange) {
    Column {
        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.toInt()) },
            valueRange = range.first.toFloat()..range.last.toFloat(),
            steps = range.last - range.first - 1
        )
        Text("$value", modifier = Modifier.align(Alignment.CenterHorizontally))
    }
}
