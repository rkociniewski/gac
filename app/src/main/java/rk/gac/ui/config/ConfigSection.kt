package rk.gac.ui.config

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import rk.gac.R
import rk.gac.enums.AdditionalMode
import rk.gac.enums.DisplayMode
import rk.gac.enums.DisplayTextRes
import rk.gac.enums.DrawMode
import rk.gac.model.Config
import kotlin.enums.EnumEntries

@Composable
fun ConfigSection(
    initialConfig: Config,
    onConfigChange: (Config) -> Unit,
) {
    var mode by remember { mutableStateOf(initialConfig.additionalMode) }
    var wordThreshold by remember { mutableIntStateOf(initialConfig.wordThreshold) }
    var prev by remember { mutableIntStateOf(initialConfig.prevCount) }
    var next by remember { mutableIntStateOf(initialConfig.nextCount) }
    var startFallback by remember { mutableIntStateOf(initialConfig.startFallback) }
    var endFallback by remember { mutableIntStateOf(initialConfig.endFallback) }
    var displayMode by remember { mutableStateOf(initialConfig.displayMode) }
    var drawMode by remember { mutableStateOf(initialConfig.drawMode) }

    // Additional mode pericopes
    Text(
        stringResource(R.string.section_additional_mode),
        style = MaterialTheme.typography.titleMedium
    )
    ModeSelector(AdditionalMode.entries, mode) { mode = it }

    if (mode == AdditionalMode.CONDITIONAL) {
        Text(
            stringResource(R.string.label_word_threshold),
            style = MaterialTheme.typography.bodyLarge
        )
        WordThresholdSelector(wordThreshold) { wordThreshold = it }
    }

    if (mode != AdditionalMode.NO) {
        ConfigSlider(stringResource(R.string.label_prev), prev) { prev = it }
        ConfigSlider(stringResource(R.string.label_next), next) { next = it }
        ConfigSlider(stringResource(R.string.label_start_fallback), startFallback) {
            startFallback = it
        }
        ConfigSlider(stringResource(R.string.label_end_fallback), endFallback) { endFallback = it }
    }

    HorizontalDivider()

    // Dark mode display mode
    Text(
        stringResource(R.string.section_display_mode),
        style = MaterialTheme.typography.titleMedium
    )
    ModeSelector(DisplayMode.entries, displayMode) { displayMode = it }

    // Drawn mode
    Text(stringResource(R.string.section_draw_mode), style = MaterialTheme.typography.titleMedium)
    ModeSelector(DrawMode.entries, drawMode) { drawMode = it }

    Spacer(modifier = Modifier.height(8.dp))

    Button(
        onClick = {
            onConfigChange(
                Config(
                    additionalMode = mode,
                    wordThreshold = wordThreshold,
                    prevCount = prev,
                    nextCount = next,
                    startFallback = startFallback,
                    endFallback = endFallback,
                    displayMode = displayMode,
                    drawMode = drawMode
                )
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        enabled = !(mode != AdditionalMode.NO && prev == 0 && next == 0)
    ) {
        Text(stringResource(R.string.save_button))
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
fun ConfigSlider(label: String, value: Int, onValueChange: (Int) -> Unit) {
    Text(label, style = MaterialTheme.typography.bodyLarge)
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
