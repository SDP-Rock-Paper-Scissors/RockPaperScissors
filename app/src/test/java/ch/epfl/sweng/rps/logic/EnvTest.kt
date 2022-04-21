package ch.epfl.sweng.rps.logic

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class EnvTest {
    @Test
    fun test() {
        assertEquals("test", Env.Test.value)
        assertEquals("prod", Env.Prod.value)
    }
}