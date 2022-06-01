package ch.epfl.sweng.rps.utils

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull


@ExperimentalCoroutinesApi
class UtilsKtTest {

    @Test
    fun consumeTest() {
        assertEquals(Unit, consume { 1 }())
    }

    @Test
    fun testBasicUseCases(): Unit = runTest(UnconfinedTestDispatcher()) {
        assertIs<SuspendResult.Failure<Int>>(guardSuspendable<Int> { throw Exception() })
        assertIs<SuspendResult.Success<Int>>(guardSuspendable { 1 })

        assertEquals(1, guardSuspendable { 1 }.getOrThrow())
        assertThrows<ArrayIndexOutOfBoundsException> { guardSuspendable { throw ArrayIndexOutOfBoundsException() }.getOrThrow() }

        assertEquals(1, guardSuspendable { 1 }.asData?.value)
        assertNull(guardSuspendable { throw Exception() }.asData?.value)

        assertEquals(3, guardSuspendable { 1 }.then { it + 2 }.getOrThrow())
        assertIs<SuspendResult.Failure<Int>>(guardSuspendable<Int> { throw  Exception() }.then { it + 2 })

        guardSuspendable { 1 }.whenIs(
            {
                assertEquals(1, it.value)
            },
            {
                throw AssertionError("Should not be called")
            }
        )
        guardSuspendable { throw ChangeNotifier.ListenerException("", null) }.whenIs(
            {
                throw AssertionError("Should not be called")
            },
            {
                assertThrows<ChangeNotifier.ListenerException> { it.getOrThrow() }
            }
        )
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