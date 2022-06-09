package ch.epfl.sweng.rps.db

import ch.epfl.sweng.rps.remote.Env
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class EnvTest {
    @Test
    fun test() {
        assertEquals("test", Env.Test.value)
        assertEquals("prod", Env.Prod.value)
    }
}