package ch.epfl.sweng.rps.utils

import ch.epfl.sweng.rps.utils.ChangeNotifier.DispatchingNotificationException
import ch.epfl.sweng.rps.utils.ChangeNotifier.ListenerNotFoundException
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.concurrent.thread
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ChangeNotifierTest {

    @Test
    fun notifyListeners() {
        val notifier = ChangeNotifier()
        val r = 0 until 1000
        val called = r.associateWith { 0 }.toMutableMap()
        val listeners = r.map { it to { called[it] = called[it]!! + 1 } }
        called.entries.forEach {
            assertEquals(0, it.value)
        }
        listeners.forEach {
            notifier.addListener(it.second)
        }

        notifier.notifyListeners()

        called.entries.forEach {
            assertEquals(1, it.value)
        }

        val removedListeners = listeners.randomElements(r.count() / 2).map { it.first }.toSet()
        listeners.forEach {
            if (it.first in removedListeners) notifier.removeListener(it.second)
        }

        notifier.notifyListeners()

        called.entries.forEach {
            assertEquals(1 + (if (it.key in removedListeners) 0 else 1), it.value)
        }
    }

    @Test
    fun testError() {
        val notifier = ChangeNotifier()

        val listener = consume { throw Exception("Ono error") }
        notifier.addListener(listener)

        assertThrows<DispatchingNotificationException> {
            notifier.notifyListeners()
        }

        notifier.removeListener(listener)
        assertThrows<ListenerNotFoundException> {
            notifier.removeListener(listener)
        }
        assertThrows<ListenerNotFoundException> {
            notifier.removeListener { "Hello" + " world" }
        }
    }

    @Test
    fun dispose() {
        val notifier = ChangeNotifier()
        val r = 0 until 1000
        val called = r.associateWith { 0 }.toMutableMap()
        val listeners = r.map { { called[it] = called[it]!! + 1 } }
        called.entries.forEach {
            assertEquals(0, it.value)
        }
        listeners.forEach {
            notifier.addListener(it)
        }

        notifier.notifyListeners()

        called.entries.forEach {
            assertEquals(1, it.value)
        }

        notifier.dispose()

        notifier.notifyListeners()

        called.entries.forEach {
            assertEquals(1, it.value)
        }
    }

    @Test
    fun testRandomElements() {
        assertTrue((0 until 10).toList().randomElements(5).size <= 5)
    }

    @Test
    fun testAwaitFor() {
        runBlocking {
            val c = Counter()
            thread {
                while (c.count < 20) {
                    c.increment()
                    Thread.sleep(100)
                }
            }
            c.awaitFor { it.count == 10 }
            assertTrue { c.count >= 10 }
        }
    }

    /**
     * This is a best effort as providing random elements from
     */
    private fun <T> List<T>.randomElements(n: Int): List<T> {
        return (0 until n).map { random() }.distinct()
    }
}


class Counter : ChangeNotifier<Counter>() {
    var count = 0
    fun increment() {
        count++
        notifyListeners()
    }
}


