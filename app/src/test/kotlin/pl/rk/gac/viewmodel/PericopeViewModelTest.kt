package pl.rk.gac.viewmodel

import android.util.Log
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import pl.rk.gac.di.SettingsRepository
import pl.rk.gac.enums.AdditionalMode
import pl.rk.gac.model.Pericope
import pl.rk.gac.model.Settings

class PericopeViewModelTest {
    private val settingsRepository = mockk<SettingsRepository>(relaxed = true)
    private lateinit var vm: PericopeViewModel

    @Before
    fun setup() {

        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.d(any(), any(), any()) } returns 0

        vm = PericopeViewModel(settingsRepository).apply {
            allPericopes.clear()
            allPericopes.addAll(
                listOf(
                    Pericope("mt_1.1-5", "Mt 1,1–5", "Rodowód", "A B C D E"),
                    Pericope("mt_1.6-10", "Mt 1,6–10", "Józef", "F G H I J"),
                    Pericope("mt_1.11-15", "Mt 1,11–15", "Narodzenie", "K L M N O"),
                    Pericope("mt_1.16-20", "Mt 1,16–20", "Pasterze", "P Q R S T"),
                    Pericope("mk_16.1-8", "Mk 16,1–8", "Rodowód", "A B C D E"),
                    Pericope("mk_16.9-20", "Mk 16,9–20", "Ukazywanie", "F G H I J"),
                )
            )
        }
    }

    @Test
    fun test_settings_update() = runTest {
        val settings = Settings(AdditionalMode.YES, prevCount = 1, nextCount = 1)
        vm.updateSettings(settings)
        Assert.assertEquals(settings, vm.settings.first())
    }

    @Test
    fun test_draw_basic_mode() = runTest {
        vm.updateSettings(Settings(AdditionalMode.NO))
        vm.drawPericope()
        Assert.assertEquals(1, vm.pericopes.value.size)
    }

    @Test
    fun test_draw_yes_mode_with_context() = runTest {
        vm.updateSettings(Settings(AdditionalMode.YES, prevCount = 1, nextCount = 1))
        vm.drawPericope()
        Assert.assertTrue(vm.pericopes.value.size in 2..3)
    }

    @Test
    fun test_draw_conditional_word_threshold() = runTest {
        vm.updateSettings(
            Settings(
                AdditionalMode.CONDITIONAL, 10, 1, 1
            )
        )
        vm.drawPericope()
        Assert.assertTrue(vm.pericopes.value.size > 1)
    }

    @Test
    fun test_draw_conditional_word_threshold_above_limit() = runTest {
        vm.updateSettings(
            Settings(AdditionalMode.CONDITIONAL, 3, 1, 1, 0, 0)
        )
        vm.drawPericope(1)

        Assert.assertEquals(1, vm.pericopes.value.size)
    }

    @Test
    fun test_draw_yes_with_start_fallback() = runTest {
        vm.updateSettings(
            Settings(
                AdditionalMode.YES, 3, 1, 1, 1, 0
            )
        )

        vm.drawPericope(forcedIndex = 0)

        Assert.assertEquals(2, vm.pericopes.value.size)
    }

    @Test
    fun test_draw_yes_with_end_fallback() = runTest {
        vm.updateSettings(
            Settings(
                AdditionalMode.YES, 3, 1, 1, 0, 1
            )
        )

        vm.drawPericope(vm.allPericopes.lastIndex)

        Assert.assertEquals(2, vm.pericopes.value.size)
    }
}
