package rk.gac.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import rk.gac.enums.AdditionalMode
import rk.gac.model.Config
import rk.gac.viewModel.PericopeViewModel

@Composable
fun PericopeScreen(viewModel: PericopeViewModel) {
    val config by viewModel.config.collectAsState()
    val pericopes by viewModel.pericopes.collectAsState()
    val selectedIndex = viewModel.selectedIndex

    var mode by remember { mutableStateOf(config.additionalMode) }
    var wordThreshold by remember { mutableIntStateOf(config.wordThreshold) }
    var prev by remember { mutableIntStateOf(config.prevCount) }
    var next by remember { mutableIntStateOf(config.nextCount) }
    var startFallback by remember { mutableIntStateOf(config.startFallback) }
    var endFallback by remember { mutableIntStateOf(config.endFallback) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Additional pericopes mode", style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AdditionalMode.entries.forEach { option ->
                FilterChip(
                    selected = mode == option,
                    onClick = { mode = option },
                    label = { Text(option.name) }
                )
            }
        }

        if (mode == AdditionalMode.CONDITIONAL) {
            Text("Próg słów", style = MaterialTheme.typography.bodyLarge)
            SliderWithLabel(value = wordThreshold, onValueChange = { wordThreshold = it }, range = 25..100)
        }

        if (mode != AdditionalMode.NO) {
            ConfigSlider("Poprzednie perykopy", prev) { prev = it }
            ConfigSlider("Następne perykopy", next) { next = it }
            ConfigSlider("Gdy początek Ewangelii", startFallback) { startFallback = it }
            ConfigSlider("Gdy koniec Ewangelii", endFallback) { endFallback = it }
        }

        Button(
            onClick = {
                viewModel.updateConfig(
                    Config(
                        additionalMode = mode,
                        wordThreshold = wordThreshold,
                        prevCount = prev,
                        nextCount = next,
                        startFallback = startFallback,
                        endFallback = endFallback
                    )
                )
                viewModel.drawPericope()
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Wylosuj perykopę")
        }

        HorizontalDivider()

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
