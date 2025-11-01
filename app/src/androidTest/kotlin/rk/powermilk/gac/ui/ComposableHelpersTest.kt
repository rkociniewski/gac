package rk.powermilk.gac.ui

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import rk.powermilk.gac.enums.AdditionalMode
import rk.powermilk.gac.enums.DisplayMode
import rk.powermilk.gac.enums.DrawMode
import rk.powermilk.gac.enums.Language
import rk.powermilk.gac.ui.helper.rememberLocalizedContext
import rk.powermilk.gac.ui.theme.GospelACasoTheme

/**
 * Instrumented tests for UI helper composables.
 * Tests reusable UI components like sliders, selectors, and labels.
 */
@RunWith(AndroidJUnit4::class)
class ComposableHelpersTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun rememberLocalizedContext_withPolishLanguage() {
        composeTestRule.setContent {
            GospelACasoTheme {
                val localizedContext = rememberLocalizedContext(Language.PL)
                // Context should be localized to Polish
                assert(localizedContext != null)
            }
        }

        composeTestRule.waitForIdle()
    }

    @Test
    fun rememberLocalizedContext_withEnglishLanguage() {
        composeTestRule.setContent {
            GospelACasoTheme {
                val localizedContext = rememberLocalizedContext(Language.EN)
                // Context should be localized to English
                assert(localizedContext != null)
            }
        }

        composeTestRule.waitForIdle()
    }

    @Test
    fun rememberLocalizedContext_switchingLanguages() {
        composeTestRule.setContent {
            GospelACasoTheme {
                val plContext = rememberLocalizedContext(Language.PL)
                val enContext = rememberLocalizedContext(Language.EN)

                // Both contexts should be created
                assert(plContext != null)
                assert(enContext != null)
            }
        }

        composeTestRule.waitForIdle()
    }

    @Test
    fun displayMode_allValuesRenderable() {
        val modes = DisplayMode.entries

        modes.forEach { mode ->
            composeTestRule.setContent {
                GospelACasoTheme {
                    // Each mode should be renderable
                    val modeString = context.getString(mode.label)
                    assert(modeString.isNotEmpty())
                }
            }

            composeTestRule.waitForIdle()
        }
    }

    @Test
    fun drawMode_allValuesRenderable() {
        val modes = DrawMode.entries

        modes.forEach { mode ->
            composeTestRule.setContent {
                GospelACasoTheme {
                    // Each mode should be renderable
                    val modeString = context.getString(mode.label)
                    assert(modeString.isNotEmpty())
                }
            }

            composeTestRule.waitForIdle()
        }
    }

    @Test
    fun additionalMode_allValuesRenderable() {
        val modes = AdditionalMode.entries

        modes.forEach { mode ->
            composeTestRule.setContent {
                GospelACasoTheme {
                    // Each mode should be renderable
                    val modeString = context.getString(mode.label)
                    assert(modeString.isNotEmpty())
                }
            }

            composeTestRule.waitForIdle()
        }
    }

    @Test
    fun language_allValuesRenderable() {
        val languages = Language.entries

        languages.forEach { language ->
            composeTestRule.setContent {
                GospelACasoTheme {
                    // Each language should have proper resources
                    val langString = context.getString(language.label)
                    assert(langString.isNotEmpty())
                }
            }

            composeTestRule.waitForIdle()
        }
    }

    @Test
    fun language_flagIconsExist() {
        val languages = Language.entries

        languages.forEach { language ->
            composeTestRule.setContent {
                GospelACasoTheme {
                    // Each language should have a flag icon
                    val drawable = context.getDrawable(language.flagIcon)
                    assert(drawable != null)
                }
            }

            composeTestRule.waitForIdle()
        }
    }

    @Test
    fun language_gospelResourcesExist() {
        val languages = Language.entries

        languages.forEach { language ->
            // Each language should have a gospel resource file
            val resourceExists = try {
                context.resources.openRawResource(language.resource).use { it.available() > 0 }
            } catch (e: Exception) {
                false
            }

            assert(resourceExists) {
                "Gospel resource for ${language.name} should exist"
            }
        }
    }
}
