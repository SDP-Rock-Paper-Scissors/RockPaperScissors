package ch.epfl.sweng.rps.db

import org.junit.Assert.assertEquals

class EnvTest {
    @org.junit.Test
    fun test() {
        assertEquals("dev", Env.Dev.toString())
        assertEquals("prod", Env.Prod.toString())
    }
}