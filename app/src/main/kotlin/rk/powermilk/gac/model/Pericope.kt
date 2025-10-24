package rk.powermilk.gac.model

import kotlinx.serialization.Serializable

/**
 * Represents a pericope, which is a section or passage from the Gospels.
 *
 * This serializable data class stores all the necessary information for displaying
 * and identifying gospel pericopes within the application.
 *
 * @property id Unique identifier for the pericope, formatted as book_chapter.verse-verse (e.g., "mt_1.1-17")
 * @property reference Human-readable biblical reference (e.g., "Mt 1,1–17")
 * @property title The title or heading of the pericope (e.g., "Rodowód Jezusa")
 * @property text Full text content of the gospel passage
 */
@Serializable
data class Pericope(
    val id: String,           // e.g. "mt_1.1-17"
    val reference: String,    // e.g. "Mt 1,1–17"
    val title: String,        // e.g. "Rodowód Jezusa"
    val text: String          // full text of Gospel
)
