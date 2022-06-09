package ch.epfl.sweng.rps.utils

import ch.epfl.sweng.rps.ActivityScenarioRuleWithSetup
import ch.epfl.sweng.rps.MainActivity
import ch.epfl.sweng.rps.ui.game.GameFragment
import io.mockk.mockk
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class LogServiceTest {
    private lateinit var logService: L.LogService

    @Before
    fun setUp() {
        L.disposeAll()
        logService = L.of("LogServiceTest")
    }

    @After
    fun tearDown() {
        L.disposeAll()
    }

    @Test
    fun testOfs() {
            assertEquals("LogServiceTest", L.of("LogServiceTest").name)
            assertEquals(SuspendResult::class.java.simpleName, L.of(SuspendResult::class.java).name)

            val activity = mockk<MainActivity>()
            assertEquals(MainActivity::class.java.simpleName, L.of(activity).name)

            val frag = mockk<GameFragment>()
            assertEquals(GameFragment::class.java.simpleName, L.of(frag).name)
    }

    @Test
    fun log() {
        logService.d("test")
        assertEquals(logService.logs.size, 1)
        assertEquals(logService.logs[0].tag, "LogServiceTest")
        assertEquals(logService.logs[0].message, "test")
    }

    @Test
    fun setSize() {
        logService.size = 10
        assertEquals(logService.size, 10)
        for (i in 0..logService.size * 2) {
            logService.d(i.toString())
        }
        assertEquals(logService.logs.size, logService.size)
        assertEquals(
            logService.logs.map { it.message },
            (0..logService.size * 2).reversed().take(logService.size).reversed()
                .map { it.toString() }
        )
        logService.size = 2
        assertEquals(logService.size, 2)
        assertEquals(logService.logs.size, logService.size)
    }

    @Test
    fun allLevels() {
        logService.d("test")
        logService.i("test")
        logService.w("test")
        logService.e("test")
        logService.v("test")

        assertEquals(logService.logs.size, 5)
    }

    @Test
    fun unregister() {
        L.disposeAll()
        for (i in 0..10) {
            L.of("LogServiceTest$i").i("test$i")
        }
        assertEquals(11, L.allInstances().size)
        L.disposeAll()
        assertEquals(0, L.allInstances().size)
    }

}