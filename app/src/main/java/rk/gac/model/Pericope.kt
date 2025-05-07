package rk.gac.model

import kotlinx.serialization.Serializable

@Serializable
data class Pericope(
    val id: String,           // e.g. "mt_1.1-17"
    val reference: String,    // e.g. "Mt 1,1–17"
    val title: String,        // e.g. "Rodowód Jezusa"
    val text: String          // full text of Gospel
)
