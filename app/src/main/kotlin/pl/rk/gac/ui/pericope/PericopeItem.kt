package pl.rk.gac.ui.pericope

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
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
fun PericopeItem(pericope: Pericope, isMain: Boolean, fontSize: Float) {
    val annotatedText = buildAnnotatedString {
        withStyle(style = SpanStyle(
            fontStyle = FontStyle.Italic,
            fontSize = (fontSize * 1.1).sp,
            color = MaterialTheme.colorScheme.primary
        )) {
            append("${pericope.reference} — ${pericope.title}")
        }
        append("\n")
        append(pericope.text)
    }

    Text(
        text = annotatedText,
        fontWeight = if (isMain) FontWeight.Bold else FontWeight.Normal,
        style = TextStyle(fontSize = fontSize.sp),
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.onBackground
    )
}
