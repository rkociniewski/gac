package rk.gac.model

import kotlinx.serialization.Serializable

@Serializable
data class Pericope(
    val id: String,           // np. "mt_1.1-17"
    val reference: String,    // np. "Mt 1,1–17"
    val title: String,        // np. "Rodowód Jezusa"
    val text: String          // pełny tekst (pojedynczy akapit)
)
