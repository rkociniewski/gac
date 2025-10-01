package pl.rk.gac.ui.pericope

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.launch
import pl.rk.gac.model.Pericope
import pl.rk.gac.model.Settings
import pl.rk.gac.util.Dimensions
import pl.rk.gac.util.Numbers

@Composable
fun PericopeContent(
    pericopes: List<Pericope>,
    selectedId: String?,
    modifier: Modifier = Modifier,
    settings: Settings,
    updateSettings: (Settings) -> Unit,
) {
    var fontSize by remember(settings.fontSize) { mutableFloatStateOf(settings.fontSize) }
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    val gestureModifier = Modifier.pointerInput(Unit) {
        awaitEachGesture {
            awaitFirstDown(requireUnconsumed = false)
            do {
                val event = awaitPointerEvent()
                val canceled = event.changes.any { it.isConsumed }

                if (!canceled && event.changes.size >= 2) {
                    val zoomChange = event.calculateZoom()
                    if (zoomChange != 1f) {
                        val newSize = (fontSize * zoomChange)
                            .coerceIn(Numbers.TWELVE.toFloat(), Numbers.FORTY_EIGHT.toFloat())
                        if (newSize != fontSize) {
                            fontSize = newSize
                            coroutineScope.launch {
                                updateSettings(settings.copy(fontSize = fontSize))
                            }
                        }
                        event.changes.forEach { it.consume() }
                    }
                }
            } while (event.changes.any { it.pressed })
        }
    }

    Column(
        modifier
            .fillMaxSize()
            .padding(Dimensions.dialogPadding)
            .then(gestureModifier)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(Dimensions.itemSpacing)
    ) {
        pericopes.forEach {
            PericopeItem(it, it.id == selectedId, fontSize)
            Spacer(Modifier.height(Dimensions.itemSpacing))
        }
    }
}
