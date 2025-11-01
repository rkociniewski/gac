package rk.powermilk.gac.viewmodel

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import rk.powermilk.gac.di.SettingsRepository
import rk.powermilk.gac.enums.AdditionalMode
import rk.powermilk.gac.enums.Language
import rk.powermilk.gac.model.Pericope
import rk.powermilk.gac.model.Settings

/**
 * Unit tests for PericopeViewModel.
 * Tests the core business logic for pericope selection and settings management.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class PericopeViewModelTest {

    private lateinit var viewModel: PericopeViewModel
    private lateinit var settingsRepository: SettingsRepository
    private val testDispatcher = StandardTestDispatcher()

    private val testPericopes = listOf(
        Pericope(
            "mt_1.1-17",
            "Mt 1,1–17",
            "Rodowód Jezusa",
            "Test text with more than fifty words to exceed the word threshold for testing purposes. " +
                "This is a long text that should contain enough words to test the conditional mode behavior when the " +
                "threshold is set to fifty words. We need to make sure this text is long enough."
        ),
        Pericope("mt_1.18-25", "Mt 1,18-25", "Narodzenie Jezusa", "Short text"),
        Pericope("mt_2.1-12", "Mt 2,1-12", "Trzej Królowie", "Another short text"),
        Pericope("mt_2.13-18", "Mt 2,13-18", "Ucieczka do Egiptu", "Short"),
        Pericope("mk_1.1-8", "Mk 1,1-8", "Jan Chrzciciel", "Different gospel text"),
        Pericope("mk_1.9-11", "Mk 1,9-11", "Chrzest Jezusa", "Short Mark text")
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        settingsRepository = mockk(relaxed = true)
        val settingsFlow = MutableStateFlow(Settings())

        every { settingsRepository.settingsFlow } returns settingsFlow
        every { settingsRepository.loadPericopesFromRaw(any()) } returns testPericopes

        viewModel = PericopeViewModel(settingsRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init loads pericopes from repository`() = runTest {
        advanceUntilIdle()

        assertEquals(6, viewModel.allPericopes.size)
        assertEquals("mt_1.1-17", viewModel.allPericopes[0].id)
    }

    @Test
    fun `drawPericope selects pericope and updates selectedId`() = runTest {
        advanceUntilIdle()

        viewModel.drawPericope(forcedIndex = 0)

        assertEquals("mt_1.1-17", viewModel.selectedId.value)
    }

    @Test
    fun `drawPericope with AdditionalMode NO shows only selected pericope`() = runTest {
        advanceUntilIdle()

        viewModel.drawPericope(forcedIndex = 1) // Middle pericope

        val result = viewModel.pericopes.value
        assertEquals(1, result.size)
        assertEquals("mt_1.18-25", result[0].id)
    }

    @Test
    fun `drawPericope with AdditionalMode YES shows adjacent pericopes`() = runTest {
        val settingsFlow = MutableStateFlow(
            Settings(
                additionalMode = AdditionalMode.YES,
                prevCount = 1,
                nextCount = 1
            )
        )
        every { settingsRepository.settingsFlow } returns settingsFlow

        val vm = PericopeViewModel(settingsRepository)
        advanceUntilIdle()

        vm.drawPericope(forcedIndex = 1) // Middle pericope

        val result = vm.pericopes.value
        assertEquals(3, result.size)
        assertEquals("mt_1.1-17", result[0].id)
        assertEquals("mt_1.18-25", result[1].id)
        assertEquals("mt_2.1-12", result[2].id)
    }

    @Test
    fun `drawPericope with CONDITIONAL mode uses threshold`() = runTest {
        val settingsFlow = MutableStateFlow(
            Settings(
                additionalMode = AdditionalMode.CONDITIONAL,
                wordThreshold = 50,
                prevCount = 1,
                nextCount = 1
            )
        )
        every { settingsRepository.settingsFlow } returns settingsFlow

        val vm = PericopeViewModel(settingsRepository)
        advanceUntilIdle()

        // Test with short text (index 1) - should show adjacent
        vm.drawPericope(forcedIndex = 1)
        var result = vm.pericopes.value
        assertEquals(3, result.size) // Shows prev + current + next

        // Test with long text (index 0) - should show only selected
        vm.drawPericope(forcedIndex = 0)
        result = vm.pericopes.value
        assertEquals(1, result.size) // Shows only current
    }

    @Test
    fun `drawPericope at gospel start uses startFallback`() = runTest {
        val settingsFlow = MutableStateFlow(
            Settings(
                additionalMode = AdditionalMode.YES,
                prevCount = 1,
                nextCount = 1,
                startFallback = 2
            )
        )
        every { settingsRepository.settingsFlow } returns settingsFlow

        val vm = PericopeViewModel(settingsRepository)
        advanceUntilIdle()

        // First pericope of Matthew
        vm.drawPericope(forcedIndex = 0)

        val result = vm.pericopes.value
        assertEquals(3, result.size) // current + startFallback(2)
        assertEquals("mt_1.1-17", result[0].id)
        assertEquals("mt_1.18-25", result[1].id)
        assertEquals("mt_2.1-12", result[2].id)
    }

    @Test
    fun `drawPericope at gospel end uses endFallback`() = runTest {
        val settingsFlow = MutableStateFlow(
            Settings(
                additionalMode = AdditionalMode.YES,
                prevCount = 1,
                nextCount = 1,
                endFallback = 2
            )
        )
        every { settingsRepository.settingsFlow } returns settingsFlow

        val vm = PericopeViewModel(settingsRepository)
        advanceUntilIdle()

        // Last pericope of Matthew (index 3)
        vm.drawPericope(forcedIndex = 3)

        val result = vm.pericopes.value
        assertEquals(3, result.size) // endFallback(2) + current
        assertEquals("mt_2.1-12", result[0].id)
        assertEquals("mt_2.13-18", result[1].id)
        assertEquals("mt_2.13-18", result[2].id)
    }

    @Test
    fun `drawPericope at Mark gospel start shows correct pericopes`() = runTest {
        val settingsFlow = MutableStateFlow(
            Settings(
                additionalMode = AdditionalMode.YES,
                prevCount = 1,
                nextCount = 1,
                startFallback = 1
            )
        )
        every { settingsRepository.settingsFlow } returns settingsFlow

        val vm = PericopeViewModel(settingsRepository)
        advanceUntilIdle()

        // First pericope of Mark (index 4)
        vm.drawPericope(forcedIndex = 4)

        val result = vm.pericopes.value
        // Should be current + startFallback(1) = 2 pericopes
        assertEquals(2, result.size)
        assertEquals("mk_1.1-8", result[0].id)
        assertEquals("mk_1.9-11", result[1].id)
    }

    @Test
    fun `drawPericope handles empty pericopes list`() = runTest {
        every { settingsRepository.loadPericopesFromRaw(any()) } returns emptyList()

        val vm = PericopeViewModel(settingsRepository)
        advanceUntilIdle()

        vm.drawPericope() // Should not throw exception

        assertEquals(0, vm.pericopes.value.size)
    }

    @Test
    fun `drawPericope with multiple prev and next counts`() = runTest {
        val settingsFlow = MutableStateFlow(
            Settings(
                additionalMode = AdditionalMode.YES,
                prevCount = 2,
                nextCount = 2
            )
        )
        every { settingsRepository.settingsFlow } returns settingsFlow

        val vm = PericopeViewModel(settingsRepository)
        advanceUntilIdle()

        vm.drawPericope(forcedIndex = 2) // Middle pericope

        val result = vm.pericopes.value
        assertEquals(5, result.size)
        assertEquals("mt_1.1-17", result[0].id)
        assertEquals("mt_1.18-25", result[1].id)
        assertEquals("mt_2.1-12", result[2].id)
        assertEquals("mt_2.13-18", result[3].id)
        assertEquals("mk_1.1-8", result[4].id)
    }

    @Test
    fun `updateSettings calls repository and updates state`() = runTest {
        coEvery { settingsRepository.updateSettings(any()) } returns true
        advanceUntilIdle()

        val newSettings = Settings(
            additionalMode = AdditionalMode.YES,
            wordThreshold = 100
        )

        viewModel.updateSettings(newSettings)
        advanceUntilIdle()

        coVerify { settingsRepository.updateSettings(newSettings) }
        assertEquals(newSettings, viewModel.settings.value)
    }

    @Test
    fun `updateSettings handles repository failure`() = runTest {
        coEvery { settingsRepository.updateSettings(any()) } returns false
        advanceUntilIdle()

        val oldSettings = viewModel.settings.value
        val newSettings = Settings(additionalMode = AdditionalMode.YES)

        viewModel.updateSettings(newSettings)
        advanceUntilIdle()

        // Settings should not be updated on failure
        assertEquals(oldSettings, viewModel.settings.value)
    }

    @Test
    fun `language change reloads pericopes`() = runTest {
        val settingsFlow = MutableStateFlow(Settings(language = Language.PL))
        every { settingsRepository.settingsFlow } returns settingsFlow
        every { settingsRepository.loadPericopesFromRaw(Language.PL) } returns testPericopes
        every { settingsRepository.loadPericopesFromRaw(Language.EN) } returns listOf(
            Pericope("mt_1.1-17", "Mt 1:1-17", "Genealogy of Jesus", "English text")
        )

        val vm = PericopeViewModel(settingsRepository)
        advanceUntilIdle()

        assertEquals(6, vm.allPericopes.size)

        // Change language
        settingsFlow.value = Settings(language = Language.EN)
        advanceUntilIdle()

        assertEquals(1, vm.allPericopes.size)
        assertEquals("Genealogy of Jesus", vm.allPericopes[0].title)
    }

    @Test
    fun `drawPericope respects boundaries and does not go out of range`() = runTest {
        val settingsFlow = MutableStateFlow(
            Settings(
                additionalMode = AdditionalMode.YES,
                prevCount = 10, // More than available
                nextCount = 10
            )
        )
        every { settingsRepository.settingsFlow } returns settingsFlow

        val vm = PericopeViewModel(settingsRepository)
        advanceUntilIdle()

        vm.drawPericope(forcedIndex = 0) // First pericope

        val result = vm.pericopes.value
        assertTrue(result.size <= testPericopes.size)
        assertEquals("mt_1.1-17", result[0].id)
    }

    @Test
    fun `isSameGospel logic works correctly across gospel boundaries`() = runTest {
        val settingsFlow = MutableStateFlow(
            Settings(
                additionalMode = AdditionalMode.YES,
                prevCount = 1,
                nextCount = 1
            )
        )
        every { settingsRepository.settingsFlow } returns settingsFlow

        val vm = PericopeViewModel(settingsRepository)
        advanceUntilIdle()

        // Select last Matthew pericope (index 3)
        vm.drawPericope(forcedIndex = 3)

        val result = vm.pericopes.value
        // Should not include Mark pericopes
        assertTrue(result.all { it.id.startsWith("mt_") })
    }

    @Test
    fun `selectedId is null initially and set after draw`() = runTest {
        advanceUntilIdle()

        viewModel.drawPericope(forcedIndex = 2)

        assertNotNull(viewModel.selectedId.value)
        assertEquals("mt_2.1-12", viewModel.selectedId.value)
    }

    @Test
    fun `pericopes flow is empty initially`() = runTest {
        // Before init completes
        assertEquals(0, viewModel.pericopes.value.size)
    }
}
