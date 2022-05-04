package ch.epfl.sweng.rps.utils

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals


class UtilsKtTest {

    @Test
    fun consumeTest() {
        assertEquals(Unit, consume { 1 }())
    }

    @Test
    fun retry() {
        runBlocking {
            assertThrows<RetryException> { retry { throw IllegalStateException() } }
            assertEquals(1, retry { 1 })
            var called = 1
            assertEquals(1, retry { called++ })
        }
    }
}