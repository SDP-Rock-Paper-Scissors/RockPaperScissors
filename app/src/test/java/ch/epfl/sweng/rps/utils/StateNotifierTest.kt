package ch.epfl.sweng.rps.utils

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class StateNotifierTest {

    @Test
    fun getValue() {
        val stateNotifier = StateNotifier(0)
        var called = 0
        stateNotifier.addListener { called++ }

        assertEquals(0, stateNotifier.value)
        stateNotifier.value = 42

        assertEquals(1, called)
        assertEquals(42, stateNotifier.value)
    }
}