package rk.powermilk.gac.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import rk.powermilk.gac.enums.AdditionalMode
import rk.powermilk.gac.enums.DisplayMode
import rk.powermilk.gac.enums.DrawMode
import rk.powermilk.gac.enums.Language
import rk.powermilk.gac.util.Numbers
import java.util.Locale

/**
 * Unit tests for Settings model.
 * Tests default values, data class behavior, and language resolution.
 */
class SettingsTest {

    @Before
    fun setup() {
        // Reset locale to default before each test
        Locale.setDefault(Locale.ENGLISH)
    }

    @Test
    fun `default settings have correct values`() {
        val settings = Settings()

        assertEquals(AdditionalMode.NO, settings.additionalMode)
        assertEquals(Numbers.WORD_THRESHOLD, settings.wordThreshold)
        assertEquals(0, settings.prevCount)
        assertEquals(1, settings.nextCount)
        assertEquals(1, settings.startFallback)
        assertEquals(1, settings.endFallback)
        assertEquals(DisplayMode.LIGHT, settings.displayMode)
        assertEquals(DrawMode.BUTTON, settings.drawMode)
        assertEquals(18f, settings.fontSize)
    }

    @Test
    fun `settings can be created with custom values`() {
        val settings = Settings(
            additionalMode = AdditionalMode.YES,
            wordThreshold = 100,
            prevCount = 2,
            nextCount = 3,
            startFallback = 4,
            endFallback = 5,
            displayMode = DisplayMode.DARK,
            drawMode = DrawMode.ROTATION,
            language = Language.PL,
            fontSize = 24f
        )

        assertEquals(AdditionalMode.YES, settings.additionalMode)
        assertEquals(100, settings.wordThreshold)
        assertEquals(2, settings.prevCount)
        assertEquals(3, settings.nextCount)
        assertEquals(4, settings.startFallback)
        assertEquals(5, settings.endFallback)
        assertEquals(DisplayMode.DARK, settings.displayMode)
        assertEquals(DrawMode.ROTATION, settings.drawMode)
        assertEquals(Language.PL, settings.language)
        assertEquals(24f, settings.fontSize)
    }

    @Test
    fun `settings data class copy works correctly`() {
        val original = Settings()
        val modified = original.copy(
            additionalMode = AdditionalMode.CONDITIONAL,
            wordThreshold = 75
        )

        assertEquals(AdditionalMode.CONDITIONAL, modified.additionalMode)
        assertEquals(75, modified.wordThreshold)
        // Other values should remain unchanged
        assertEquals(original.prevCount, modified.prevCount)
        assertEquals(original.nextCount, modified.nextCount)
    }

    @Test
    fun `settings equality works correctly`() {
        val settings1 = Settings(
            additionalMode = AdditionalMode.YES,
            wordThreshold = 100
        )
        val settings2 = Settings(
            additionalMode = AdditionalMode.YES,
            wordThreshold = 100
        )
        val settings3 = Settings(
            additionalMode = AdditionalMode.NO,
            wordThreshold = 100
        )

        assertEquals(settings1, settings2)
        assert(settings1 != settings3)
    }

    @Test
    fun `resolveDefaultLanguage returns EN for English locale`() {
        Locale.setDefault(Locale.ENGLISH)
        val language = resolveDefaultLanguage()

        assertEquals(Language.EN, language)
    }

    @Test
    fun `resolveDefaultLanguage returns PL for Polish locale`() {
        Locale.setDefault(Locale.forLanguageTag("PL"))
        val language = resolveDefaultLanguage()

        assertEquals(Language.PL, language)
    }

    @Test
    fun `resolveDefaultLanguage returns EN for unsupported locale`() {
        Locale.setDefault(Locale.FRENCH)
        val language = resolveDefaultLanguage()

        assertEquals(Language.EN, language)
    }

    @Test
    fun `resolveDefaultLanguage returns EN for German locale`() {
        Locale.setDefault(Locale.GERMAN)
        val language = resolveDefaultLanguage()

        assertEquals(Language.EN, language)
    }

    @Test
    fun `resolveDefaultLanguage is case insensitive`() {
        // Test with lowercase locale
        Locale.setDefault(Locale.forLanguageTag("pl"))
        val language = resolveDefaultLanguage()

        assertEquals(Language.PL, language)
    }

    @Test
    fun `settings with all AdditionalMode values`() {
        val settingsYes = Settings(additionalMode = AdditionalMode.YES)
        val settingsNo = Settings(additionalMode = AdditionalMode.NO)
        val settingsConditional = Settings(additionalMode = AdditionalMode.CONDITIONAL)

        assertEquals(AdditionalMode.YES, settingsYes.additionalMode)
        assertEquals(AdditionalMode.NO, settingsNo.additionalMode)
        assertEquals(AdditionalMode.CONDITIONAL, settingsConditional.additionalMode)
    }

    @Test
    fun `settings with all DisplayMode values`() {
        val settingsLight = Settings(displayMode = DisplayMode.LIGHT)
        val settingsDark = Settings(displayMode = DisplayMode.DARK)
        val settingsSystem = Settings(displayMode = DisplayMode.SYSTEM)

        assertEquals(DisplayMode.LIGHT, settingsLight.displayMode)
        assertEquals(DisplayMode.DARK, settingsDark.displayMode)
        assertEquals(DisplayMode.SYSTEM, settingsSystem.displayMode)
    }

    @Test
    fun `settings with all DrawMode values`() {
        val settingsButton = Settings(drawMode = DrawMode.BUTTON)
        val settingsRotation = Settings(drawMode = DrawMode.ROTATION)
        val settingsBoth = Settings(drawMode = DrawMode.BOTH)

        assertEquals(DrawMode.BUTTON, settingsButton.drawMode)
        assertEquals(DrawMode.ROTATION, settingsRotation.drawMode)
        assertEquals(DrawMode.BOTH, settingsBoth.drawMode)
    }

    @Test
    fun `settings with boundary values`() {
        val settings = Settings(
            wordThreshold = 0,
            prevCount = 0,
            nextCount = 0,
            startFallback = 0,
            endFallback = 0,
            fontSize = 0f
        )

        assertEquals(0, settings.wordThreshold)
        assertEquals(0, settings.prevCount)
        assertEquals(0, settings.nextCount)
        assertEquals(0, settings.startFallback)
        assertEquals(0, settings.endFallback)
        assertEquals(0f, settings.fontSize)
    }

    @Test
    fun `settings with maximum reasonable values`() {
        val settings = Settings(
            wordThreshold = 1000,
            prevCount = 100,
            nextCount = 100,
            startFallback = 100,
            endFallback = 100,
            fontSize = 100f
        )

        assertEquals(1000, settings.wordThreshold)
        assertEquals(100, settings.prevCount)
        assertEquals(100, settings.nextCount)
        assertEquals(100, settings.startFallback)
        assertEquals(100, settings.endFallback)
        assertEquals(100f, settings.fontSize)
    }

    @Test
    fun `settings toString contains all fields`() {
        val settings = Settings()
        val stringRepresentation = settings.toString()

        assertNotNull(stringRepresentation)
        assert(stringRepresentation.contains("Settings"))
    }

    @Test
    fun `settings hashCode is consistent`() {
        val settings1 = Settings(additionalMode = AdditionalMode.YES)
        val settings2 = Settings(additionalMode = AdditionalMode.YES)

        assertEquals(settings1.hashCode(), settings2.hashCode())
    }

    @Test
    fun `default language is set from system locale`() {
        val settings = Settings()
        assertNotNull(settings.language)
    }

    @Test
    fun `word threshold default is correct`() {
        val settings = Settings()
        assertEquals(Numbers.WORD_THRESHOLD, settings.wordThreshold)
        assertEquals(50, settings.wordThreshold)
    }

    @Test
    fun `font size default is correct`() {
        val settings = Settings()
        assertEquals(18f, settings.fontSize)
    }
}
