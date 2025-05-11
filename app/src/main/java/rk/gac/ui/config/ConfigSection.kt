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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
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
    config: Config,
    updateConfig: (Config) -> Unit,
    onClose: () -> Unit
) {
    val view = LocalView.current

    // Additional mode pericopes
    Text(
        stringResource(R.string.section_additional_mode),
        style = MaterialTheme.typography.titleMedium
    )
    ModeSelector(AdditionalMode.entries, config.additionalMode) {
        updateConfig(config.copy(additionalMode = it))
    }

    if (config.additionalMode == AdditionalMode.CONDITIONAL) {
        Text(
            stringResource(R.string.label_word_threshold),
            style = MaterialTheme.typography.bodyLarge
        )
        WordThresholdSelector(config.wordThreshold) {
            updateConfig(config.copy(wordThreshold = it))
        }
    }

    if (config.additionalMode != AdditionalMode.NO) {
        ConfigSlider(stringResource(R.string.label_prev), config.prevCount) {
            updateConfig(config.copy(prevCount = it))
        }
        ConfigSlider(stringResource(R.string.label_next), config.nextCount) {
            updateConfig(config.copy(nextCount = it))
        }
        ConfigSlider(stringResource(R.string.label_start_fallback), config.startFallback) {
            updateConfig(config.copy(startFallback = it))
        }
        ConfigSlider(stringResource(R.string.label_end_fallback), config.endFallback) {
            updateConfig(config.copy(endFallback = it))
        }
    }

    HorizontalDivider()

    // Dark mode display mode
    Text(
        stringResource(R.string.section_display_mode),
        style = MaterialTheme.typography.titleMedium
    )
    ModeSelector(DisplayMode.entries, config.displayMode) {
        updateConfig(config.copy(displayMode = it))
    }

    // Drawn mode
    Text(stringResource(R.string.section_draw_mode), style = MaterialTheme.typography.titleMedium)
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

    // Automatically trigger onClose when user taps outside the dialog
    LaunchedEffect(view) {
        view.rootView?.viewTreeObserver?.addOnWindowFocusChangeListener { hasFocus ->
            if (!hasFocus) {
                onClose()
            }
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
