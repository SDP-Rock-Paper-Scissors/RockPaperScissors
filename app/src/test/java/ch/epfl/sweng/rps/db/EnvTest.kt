package ch.epfl.sweng.rps.db

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test


class EnvTest {
    @Test
    fun test() {
        assertEquals("dev", Env.DEV.toString())
        assertEquals("prod", Env.PROD.toString())
    }
}