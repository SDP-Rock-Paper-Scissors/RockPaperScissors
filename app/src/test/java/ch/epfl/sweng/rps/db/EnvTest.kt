package ch.epfl.sweng.rps.db

import org.junit.Assert.assertEquals

class EnvTest {
    @org.junit.Test
    fun test() {
        assertEquals("dev", Env.DEV.toString())
        assertEquals("prod", Env.PROD.toString())
    }
}