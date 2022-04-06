package ch.epfl.sweng.rps.db

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class EnvTest {
    @Test
    fun test() {
        assertEquals("dev", Env.Dev.value)
        assertEquals("prod", Env.Prod.value)
    }
}