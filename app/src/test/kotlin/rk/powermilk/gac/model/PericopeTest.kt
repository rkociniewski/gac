package rk.powermilk.gac.model

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for Pericope model.
 * Tests serialization, deserialization, and data class behavior.
 */
class PericopeTest {

    @Test
    fun `pericope can be created with all fields`() {
        val pericope = Pericope(
            id = "mt_1.1-17",
            reference = "Mt 1,1–17",
            title = "Rodowód Jezusa",
            text = "Rodowód Jezusa Chrystusa..."
        )

        assertEquals("mt_1.1-17", pericope.id)
        assertEquals("Mt 1,1–17", pericope.reference)
        assertEquals("Rodowód Jezusa", pericope.title)
        assertEquals("Rodowód Jezusa Chrystusa...", pericope.text)
    }

    @Test
    fun `pericope serialization to JSON works correctly`() {
        val pericope = Pericope(
            id = "mt_1.1-17",
            reference = "Mt 1,1–17",
            title = "Rodowód Jezusa",
            text = "Test text"
        )

        val json = Json.encodeToString(pericope)

        assertTrue(json.contains("\"id\":\"mt_1.1-17\""))
        assertTrue(json.contains("\"reference\":\"Mt 1,1–17\""))
        assertTrue(json.contains("\"title\":\"Rodowód Jezusa\""))
        assertTrue(json.contains("\"text\":\"Test text\""))
    }

    @Test
    fun `pericope deserialization from JSON works correctly`() {
        val json = """
            {
                "id": "mt_1.1-17",
                "reference": "Mt 1,1–17",
                "title": "Rodowód Jezusa",
                "text": "Test text"
            }
        """.trimIndent()

        val pericope = Json.decodeFromString<Pericope>(json)

        assertEquals("mt_1.1-17", pericope.id)
        assertEquals("Mt 1,1–17", pericope.reference)
        assertEquals("Rodowód Jezusa", pericope.title)
        assertEquals("Test text", pericope.text)
    }

    @Test
    fun `pericope list can be deserialized from JSON array`() {
        val json = """
            [
                {
                    "id": "mt_1.1-17",
                    "reference": "Mt 1,1–17",
                    "title": "Rodowód Jezusa",
                    "text": "Text 1"
                },
                {
                    "id": "mt_1.18-25",
                    "reference": "Mt 1,18-25",
                    "title": "Narodzenie Jezusa",
                    "text": "Text 2"
                }
            ]
        """.trimIndent()

        val pericopes = Json.decodeFromString<List<Pericope>>(json)

        assertEquals(2, pericopes.size)
        assertEquals("mt_1.1-17", pericopes[0].id)
        assertEquals("mt_1.18-25", pericopes[1].id)
    }

    @Test
    fun `pericope data class copy works correctly`() {
        val original = Pericope(
            id = "mt_1.1-17",
            reference = "Mt 1,1–17",
            title = "Rodowód Jezusa",
            text = "Original text"
        )

        val modified = original.copy(text = "Modified text")

        assertEquals("mt_1.1-17", modified.id)
        assertEquals("Mt 1,1–17", modified.reference)
        assertEquals("Rodowód Jezusa", modified.title)
        assertEquals("Modified text", modified.text)
    }

    @Test
    fun `pericope equality works correctly`() {
        val pericope1 = Pericope(
            id = "mt_1.1-17",
            reference = "Mt 1,1–17",
            title = "Rodowód Jezusa",
            text = "Test text"
        )
        val pericope2 = Pericope(
            id = "mt_1.1-17",
            reference = "Mt 1,1–17",
            title = "Rodowód Jezusa",
            text = "Test text"
        )
        val pericope3 = Pericope(
            id = "mt_1.18-25",
            reference = "Mt 1,18-25",
            title = "Different",
            text = "Different text"
        )

        assertEquals(pericope1, pericope2)
        assert(pericope1 != pericope3)
    }

    @Test
    fun `pericope id format for Matthew gospel`() {
        val pericope = Pericope(
            id = "mt_1.1-17",
            reference = "Mt 1,1–17",
            title = "Test",
            text = "Text"
        )

        assertTrue(pericope.id.startsWith("mt_"))
    }

    @Test
    fun `pericope id format for Mark gospel`() {
        val pericope = Pericope(
            id = "mk_1.1-8",
            reference = "Mk 1,1-8",
            title = "Test",
            text = "Text"
        )

        assertTrue(pericope.id.startsWith("mk_"))
    }

    @Test
    fun `pericope id format for Luke gospel`() {
        val pericope = Pericope(
            id = "lk_1.1-4",
            reference = "Łk 1,1-4",
            title = "Test",
            text = "Text"
        )

        assertTrue(pericope.id.startsWith("lk_"))
    }

    @Test
    fun `pericope id format for John gospel`() {
        val pericope = Pericope(
            id = "jn_1.1-18",
            reference = "J 1,1-18",
            title = "Test",
            text = "Text"
        )

        assertTrue(pericope.id.startsWith("jn_"))
    }

    @Test
    fun `pericope handles special characters in text`() {
        val text = "Jezus powiedział: «Nie bójcie się!» Uczniowie słuchali..."
        val pericope = Pericope(
            id = "mt_14.22-33",
            reference = "Mt 14,22-33",
            title = "Test",
            text = text
        )

        assertEquals(text, pericope.text)
    }

    @Test
    fun `pericope handles long text`() {
        val longText = "A".repeat(10000)
        val pericope = Pericope(
            id = "mt_1.1-17",
            reference = "Mt 1,1–17",
            title = "Test",
            text = longText
        )

        assertEquals(10000, pericope.text.length)
    }

    @Test
    fun `pericope handles empty strings`() {
        val pericope = Pericope(
            id = "",
            reference = "",
            title = "",
            text = ""
        )

        assertEquals("", pericope.id)
        assertEquals("", pericope.reference)
        assertEquals("", pericope.title)
        assertEquals("", pericope.text)
    }

    @Test
    fun `pericope toString contains all fields`() {
        val pericope = Pericope(
            id = "mt_1.1-17",
            reference = "Mt 1,1–17",
            title = "Rodowód Jezusa",
            text = "Test text"
        )

        val stringRepresentation = pericope.toString()

        assertNotNull(stringRepresentation)
        assertTrue(stringRepresentation.contains("Pericope"))
    }

    @Test
    fun `pericope hashCode is consistent`() {
        val pericope1 = Pericope("mt_1.1-17", "Mt 1,1–17", "Title", "Text")
        val pericope2 = Pericope("mt_1.1-17", "Mt 1,1–17", "Title", "Text")

        assertEquals(pericope1.hashCode(), pericope2.hashCode())
    }

    @Test
    fun `pericope with Unicode characters`() {
        val pericope = Pericope(
            id = "mt_1.1-17",
            reference = "Mt 1,1–17",
            title = "Tęskt żółć ąę",
            text = "Łódź 中文 العربية"
        )

        assertEquals("Tęskt żółć ąę", pericope.title)
        assertEquals("Łódź 中文 العربية", pericope.text)
    }

    @Test
    fun `pericope JSON handles Unicode correctly`() {
        val pericope = Pericope(
            id = "mt_1.1-17",
            reference = "Mt 1,1–17",
            title = "Ąćęłńóśźż",
            text = "Unicode test"
        )

        val json = Json.encodeToString(pericope)
        val decoded = Json.decodeFromString<Pericope>(json)

        assertEquals(pericope.title, decoded.title)
    }

    @Test
    fun `pericope with verse ranges in id`() {
        val testIds = listOf(
            "mt_1.1-17",
            "mt_5.1-12",
            "mk_16.1-8",
            "lk_2.1-20",
            "jn_1.1-18"
        )

        testIds.forEach { id ->
            val pericope = Pericope(id, "Ref", "Title", "Text")
            assertTrue(pericope.id.contains("."))
            assertTrue(pericope.id.contains("-"))
        }
    }
}
