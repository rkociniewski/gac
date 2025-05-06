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
import androidx.compose.ui.unit.dp
import rk.gac.enums.AdditionalMode
import rk.gac.enums.DisplayMode
import rk.gac.enums.DrawMode
import rk.gac.model.Config

@Composable
fun ConfigSection(
    initialConfig: Config,
    onConfigChange: (Config) -> Unit,
    onDraw: () -> Unit,
    showSaveOnly: Boolean = false
) {
    var mode by remember { mutableStateOf(initialConfig.additionalMode) }
    var wordThreshold by remember { mutableIntStateOf(initialConfig.wordThreshold) }
    var prev by remember { mutableIntStateOf(initialConfig.prevCount) }
    var next by remember { mutableIntStateOf(initialConfig.nextCount) }
    var startFallback by remember { mutableIntStateOf(initialConfig.startFallback) }
    var endFallback by remember { mutableIntStateOf(initialConfig.endFallback) }
    var displayMode by remember { mutableStateOf(initialConfig.displayMode) }
    var drawMode by remember { mutableStateOf(initialConfig.drawMode) }

    // Tryb dodatkowych perykop
    Text("Tryb dodatkowych perykop", style = MaterialTheme.typography.titleMedium)
    ModeSelector(AdditionalMode.entries.toTypedArray(), mode) { mode = it }

    if (mode == AdditionalMode.CONDITIONAL) {
        Text("Próg słów", style = MaterialTheme.typography.bodyLarge)
        WordThresholdSelector(wordThreshold) { wordThreshold = it }
    }

    if (mode != AdditionalMode.NO) {
        ConfigSlider("Poprzednie perykopy", prev) { prev = it }
        ConfigSlider("Następne perykopy", next) { next = it }
        ConfigSlider("Fallback na początku", startFallback) { startFallback = it }
        ConfigSlider("Fallback na końcu", endFallback) { endFallback = it }
    }

    HorizontalDivider()

    // Tryb wyświetlania
    Text("Tryb wyświetlania", style = MaterialTheme.typography.titleMedium)
    ModeSelector(DisplayMode.entries.toTypedArray(), displayMode) { displayMode = it }

    // Tryb losowania
    Text("Tryb losowania", style = MaterialTheme.typography.titleMedium)
    ModeSelector(DrawMode.entries.toTypedArray(), drawMode) { drawMode = it }

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
            if (!showSaveOnly) onDraw()
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        enabled = !(mode != AdditionalMode.NO && prev == 0 && next == 0)
    ) {
        Text(if (showSaveOnly) "Zapisz zmiany" else "Wylosuj perykopę")
    }
}


@Composable
fun SectionTitle(text: String) {
    Text(text, style = MaterialTheme.typography.titleMedium)
}

@Composable
fun <T : Enum<T>> ModeSelector(options: Array<T>, selected: T, onSelect: (T) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEach { opt ->
            FilterChip(
                selected = selected == opt,
                onClick = { onSelect(opt) },
                label = { Text(opt.name) }
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
