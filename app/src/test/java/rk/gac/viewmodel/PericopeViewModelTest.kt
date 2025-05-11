package rk.gac.viewmodel

import android.app.Application
import android.util.Log
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import rk.gac.enums.AdditionalMode
import rk.gac.model.Config
import rk.gac.model.Pericope

class PericopeViewModelTest {
    private val mockApp = mockk<Application>(relaxed = true)
    private lateinit var vm: PericopeViewModel

    @Before
    fun setup() {

        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.d(any(), any(), any()) } returns 0

        vm = PericopeViewModel(mockApp).apply {
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
    fun test_config_update() = runTest {
        val config = Config(AdditionalMode.YES, prevCount = 1, nextCount = 1)
        vm.updateConfig(config)
        assertEquals(config, vm.config.first())
    }

    @Test
    fun test_draw_basic_mode() = runTest {
        vm.updateConfig(Config(AdditionalMode.NO))
        vm.drawPericope()
        assertEquals(1, vm.pericopes.value.size)
    }

    @Test
    fun test_draw_yes_mode_with_context() = runTest {
        vm.updateConfig(Config(AdditionalMode.YES, prevCount = 1, nextCount = 1))
        vm.drawPericope()
        assertTrue(vm.pericopes.value.size in 2..3)
    }

    @Test
    fun test_draw_conditional_word_threshold() = runTest {
        vm.updateConfig(
            Config(
                AdditionalMode.CONDITIONAL, 10, 1, 1
            )
        )
        vm.drawPericope()
        assertTrue(vm.pericopes.value.size > 1)
    }

    @Test
    fun test_draw_conditional_word_threshold_above_limit() = runTest {
        vm.updateConfig(
            Config(AdditionalMode.CONDITIONAL, 3, 1, 1, 0, 0)
        )
        vm.drawPericope(1)

        assertEquals(1, vm.pericopes.value.size)
    }

    @Test
    fun test_draw_yes_with_start_fallback() = runTest {
        vm.updateConfig(
            Config(
                AdditionalMode.YES, 3, 1, 1, 1, 0
            )
        )

        vm.drawPericope(forcedIndex = 0)

        assertEquals(2, vm.pericopes.value.size)
    }

    @Test
    fun test_draw_yes_with_end_fallback() = runTest {
        vm.updateConfig(
            Config(
                AdditionalMode.YES, 3, 1, 1, 0, 1
            )
        )

        vm.drawPericope(vm.allPericopes.lastIndex)

        assertEquals(2, vm.pericopes.value.size)
    }
}
