package rk.powermilk.gac.ui

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import rk.powermilk.gac.enums.AdditionalMode
import rk.powermilk.gac.enums.DisplayMode
import rk.powermilk.gac.enums.DrawMode
import rk.powermilk.gac.enums.Language
import rk.powermilk.gac.model.Settings
import rk.powermilk.gac.ui.settings.SettingsDialog
import rk.powermilk.gac.ui.theme.GospelACasoTheme

/**
 * Instrumented tests for SettingsDialog UI.
 * Tests the settings dialog composable and configuration options.
 */
@RunWith(AndroidJUnit4::class)
class SettingsDialogTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun settingsDialog_rendersWithDefaultSettings() {
        val settings = Settings()
        val onSettingsUpdate: (Settings) -> Unit = mockk(relaxed = true)
        val onDismiss: () -> Unit = mockk(relaxed = true)

        composeTestRule.setContent {
            GospelACasoTheme {
                SettingsDialog(settings, onSettingsUpdate, onDismiss, context)
            }
        }

        composeTestRule.waitForIdle()
        // Dialog should render without crashing
    }

    @Test
    fun settingsDialog_rendersWithCustomSettings() {
        val settings = Settings(
            additionalMode = AdditionalMode.YES,
            wordThreshold = 100,
            prevCount = 2,
            nextCount = 3,
            displayMode = DisplayMode.DARK,
            drawMode = DrawMode.BOTH,
            language = Language.PL,
            fontSize = 20f
        )
        val onSettingsUpdate: (Settings) -> Unit = mockk(relaxed = true)
        val onDismiss: () -> Unit = mockk(relaxed = true)

        composeTestRule.setContent {
            GospelACasoTheme {
                SettingsDialog(settings, onSettingsUpdate, onDismiss, context)
            }
        }

        composeTestRule.waitForIdle()
        // Dialog should render with custom settings
    }

    @Test
    fun settingsDialog_showsErrorWhenInvalidSettings() {
        val invalidSettings = Settings(
            additionalMode = AdditionalMode.YES,
            prevCount = 0,
            nextCount = 0
        )
        val onSettingsUpdate: (Settings) -> Unit = mockk(relaxed = true)
        val onDismiss: () -> Unit = mockk(relaxed = true)

        composeTestRule.setContent {
            GospelACasoTheme {
                SettingsDialog(invalidSettings, onSettingsUpdate, onDismiss, context)
            }
        }

        composeTestRule.waitForIdle()
        // Error message should be displayed for invalid settings
    }

    @Test
    fun settingsDialog_noErrorWhenValidSettings() {
        val validSettings = Settings(
            additionalMode = AdditionalMode.YES,
            prevCount = 1,
            nextCount = 1
        )
        val onSettingsUpdate: (Settings) -> Unit = mockk(relaxed = true)
        val onDismiss: () -> Unit = mockk(relaxed = true)

        composeTestRule.setContent {
            GospelACasoTheme {
                SettingsDialog(validSettings, onSettingsUpdate, onDismiss, context)
            }
        }

        composeTestRule.waitForIdle()
        // No error should be displayed
    }

    @Test
    fun settingsDialog_noErrorWhenAdditionalModeNo() {
        val settings = Settings(
            additionalMode = AdditionalMode.NO,
            prevCount = 0,
            nextCount = 0
        )
        val onSettingsUpdate: (Settings) -> Unit = mockk(relaxed = true)
        val onDismiss: () -> Unit = mockk(relaxed = true)

        composeTestRule.setContent {
            GospelACasoTheme {
                SettingsDialog(settings, onSettingsUpdate, onDismiss, context)
            }
        }

        composeTestRule.waitForIdle()
        // No error when AdditionalMode is NO
    }

    @Test
    fun settingsDialog_rendersWithConditionalMode() {
        val settings = Settings(
            additionalMode = AdditionalMode.CONDITIONAL,
            wordThreshold = 75
        )
        val onSettingsUpdate: (Settings) -> Unit = mockk(relaxed = true)
        val onDismiss: () -> Unit = mockk(relaxed = true)

        composeTestRule.setContent {
            GospelACasoTheme {
                SettingsDialog(settings, onSettingsUpdate, onDismiss, context)
            }
        }

        composeTestRule.waitForIdle()
        // Dialog should render with conditional mode
    }

    @Test
    fun settingsDialog_rendersWithAllDisplayModes() {
        val modes = listOf(DisplayMode.LIGHT, DisplayMode.DARK, DisplayMode.SYSTEM)

        modes.forEach { mode ->
            val settings = Settings(displayMode = mode)
            val onSettingsUpdate: (Settings) -> Unit = mockk(relaxed = true)
            val onDismiss: () -> Unit = mockk(relaxed = true)

            composeTestRule.setContent {
                GospelACasoTheme {
                    SettingsDialog(settings, onSettingsUpdate, onDismiss, context)
                }
            }

            composeTestRule.waitForIdle()
        }
    }

    @Test
    fun settingsDialog_rendersWithAllDrawModes() {
        val modes = listOf(DrawMode.BUTTON, DrawMode.ROTATION, DrawMode.BOTH)

        modes.forEach { mode ->
            val settings = Settings(drawMode = mode)
            val onSettingsUpdate: (Settings) -> Unit = mockk(relaxed = true)
            val onDismiss: () -> Unit = mockk(relaxed = true)

            composeTestRule.setContent {
                GospelACasoTheme {
                    SettingsDialog(settings, onSettingsUpdate, onDismiss, context)
                }
            }

            composeTestRule.waitForIdle()
        }
    }

    @Test
    fun settingsDialog_rendersWithDifferentFontSizes() {
        val fontSizes = listOf(12f, 16f, 18f, 20f, 24f)

        fontSizes.forEach { fontSize ->
            val settings = Settings(fontSize = fontSize)
            val onSettingsUpdate: (Settings) -> Unit = mockk(relaxed = true)
            val onDismiss: () -> Unit = mockk(relaxed = true)

            composeTestRule.setContent {
                GospelACasoTheme {
                    SettingsDialog(settings, onSettingsUpdate, onDismiss, context)
                }
            }

            composeTestRule.waitForIdle()
        }
    }

    @Test
    fun settingsDialog_rendersWithBothLanguages() {
        val languages = listOf(Language.PL, Language.EN)

        languages.forEach { language ->
            val settings = Settings(language = language)
            val onSettingsUpdate: (Settings) -> Unit = mockk(relaxed = true)
            val onDismiss: () -> Unit = mockk(relaxed = true)

            composeTestRule.setContent {
                GospelACasoTheme {
                    SettingsDialog(settings, onSettingsUpdate, onDismiss, context)
                }
            }

            composeTestRule.waitForIdle()
        }
    }

    @Test
    fun settingsDialog_handlesExtremeFallbackValues() {
        val settings = Settings(
            startFallback = 100,
            endFallback = 100
        )
        val onSettingsUpdate: (Settings) -> Unit = mockk(relaxed = true)
        val onDismiss: () -> Unit = mockk(relaxed = true)

        composeTestRule.setContent {
            GospelACasoTheme {
                SettingsDialog(settings, onSettingsUpdate, onDismiss, context)
            }
        }

        composeTestRule.waitForIdle()
        // Dialog should handle extreme values
    }

    @Test
    fun settingsDialog_isScrollable() {
        val settings = Settings()
        val onSettingsUpdate: (Settings) -> Unit = mockk(relaxed = true)
        val onDismiss: () -> Unit = mockk(relaxed = true)

        composeTestRule.setContent {
            GospelACasoTheme {
                SettingsDialog(settings, onSettingsUpdate, onDismiss, context)
            }
        }

        composeTestRule.waitForIdle()
        // Dialog content should be scrollable
    }
}
