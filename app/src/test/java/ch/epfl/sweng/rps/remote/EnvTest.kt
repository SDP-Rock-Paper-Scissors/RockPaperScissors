package ch.epfl.sweng.rps.remote

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class EnvTest {
    @Test
    fun test() {
        assertEquals("test", Env.Test.value)
        assertEquals("prod", Env.Prod.value)
    }
}