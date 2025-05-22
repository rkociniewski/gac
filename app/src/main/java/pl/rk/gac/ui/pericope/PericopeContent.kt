package pl.rk.gac.ui.pericope

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import pl.rk.gac.model.Pericope
import pl.rk.gac.util.Dimensions


/**
 * Main content area displaying the list of pericopes.
 *
 * This composable creates a scrollable column of pericope items, with the selected
 * pericope visually distinguished from others.
 *
 * @param pericopes List of pericopes to display
 * @param selectedId ID of the currently selected pericope
 * @param modifier Modifier to be applied to the content container
 */
@Composable
fun PericopeContent(
    pericopes: List<Pericope>,
    selectedId: String?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier
            .fillMaxSize()
            .padding(Dimensions.dialogPadding)
            .verticalScroll(rememberScrollState()), Arrangement.spacedBy(Dimensions.itemSpacing)
    ) {
        pericopes.forEach { pericope ->
            PericopeItem(pericope, pericope.id == selectedId)
            Spacer(Modifier.height(Dimensions.itemSpacing))
        }
    }
}
