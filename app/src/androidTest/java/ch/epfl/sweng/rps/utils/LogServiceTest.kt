package ch.epfl.sweng.rps.utils

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class LogServiceTest {
    private lateinit var logService: L.LogService

    @Before
    fun setUp() {
        logService = L.of("LogServiceTest")
    }

    @After
    fun tearDown() {
        L.unregister(logService)
        L.dispose(logService.name)
    }

    @Test
    fun log() {
        logService.d("test")
        assertEquals(logService.logs.size, 1)
        assertEquals(logService.logs[0].tag, "LogServiceTest")
        assertEquals(logService.logs[0].message, "test")
    }

    @Test
    fun setSize() {
        logService.size = 10
        assertEquals(logService.size, 10)
        for (i in 0..logService.size * 2) {
            logService.d(i.toString())
        }
        assertEquals(logService.logs.size, logService.size)
        assertEquals(
            logService.logs.map { it.message },
            (0..logService.size * 2).reversed().take(logService.size).reversed()
                .map { it.toString() }
        )
        logService.size = 2
        assertEquals(logService.size, 2)
        assertEquals(logService.logs.size, logService.size)
    }

    @Test
    fun allLevels() {
        logService.d("test")
        logService.i("test")
        logService.w("test")
        logService.e("test")
        logService.v("test")

        assertEquals(logService.logs.size, 5)
    }

}