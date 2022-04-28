package ch.epfl.sweng.rps.utils


import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class EmulatorConfigTest {

    @Test
    fun get() {
        val config = EmulatorConfig("localhost", 5554)
        assertEquals("10.0.2.2", config.host)
        assertEquals(5554, config.port)

        val config2 = EmulatorConfig("1.1.1.1", 5554)
        assertEquals("1.1.1.1", config2.host)
        assertEquals(5554, config2.port)
    }
}