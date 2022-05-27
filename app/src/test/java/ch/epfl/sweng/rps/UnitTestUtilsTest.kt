package ch.epfl.sweng.rps

import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class UnitTestUtilsTest {

    @Test
    fun assertThrowsTest() {
        assertThrows(IllegalArgumentException::class.java) {
            throw IllegalArgumentException()
        }
    }
}