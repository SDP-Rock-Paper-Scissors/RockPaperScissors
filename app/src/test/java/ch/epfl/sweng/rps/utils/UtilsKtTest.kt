package ch.epfl.sweng.rps.utils

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertIs


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

    @Test
    fun options() {
        assertIs<Option.None<Int>>(Option.fromNullable<Int>(null))
        assertIs<Option.Some<Int>>(Option.fromNullable(1))
    }
}