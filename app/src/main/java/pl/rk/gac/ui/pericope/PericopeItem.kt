package pl.rk.gac.ui.pericope

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import pl.rk.gac.model.Pericope

/**
 * Individual pericope item display.
 *
 * This composable displays a single pericope with its reference, title, and text content.
 * The main (selected) pericope is displayed with bold text for emphasis.
 *
 * @param pericope The pericope data to display
 * @param isMain Whether this is the main (selected) pericope
 */
@Composable
fun PericopeItem(pericope: Pericope, isMain: Boolean) {
    Text(
        text = "${pericope.reference} — ${pericope.title}\n${pericope.text}",
        fontWeight = if (isMain) FontWeight.Bold else FontWeight.Normal,
        style = MaterialTheme.typography.bodyLarge
    )
}
