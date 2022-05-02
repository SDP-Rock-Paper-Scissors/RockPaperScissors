package ch.epfl.sweng.rps.utils

import org.junit.After
import org.junit.Assert.*
import org.junit.Before

import org.junit.Test

class LogServiceTest {

    @Before
    fun setUp() {
    }

    @After
    fun tearDown() {
        LogService.clear()
    }

    @Test
    fun log() {
        LogService.d("LoginServiceTest", "test")
        assertEquals(LogService.logs.size, 1)
        assertEquals(LogService.logs[0].tag, "LoginServiceTest")
        assertEquals(LogService.logs[0].message, "test")
    }

    @Test
    fun setSize() {
        LogService.size = 10
        assertEquals(LogService.size, 10)
        for (i in 0..LogService.size * 2) {
            LogService.d("LoginServiceTest", "test")
        }
        assertEquals(LogService.logs.size, LogService.size)
        LogService.size = 2
        assertEquals(LogService.size, 2)
        assertEquals(LogService.logs.size, LogService.size)
    }

    @Test
    fun allLevels() {
        LogService.d("LoginServiceTest", "test")
        LogService.i("LoginServiceTest", "test")
        LogService.w("LoginServiceTest", "test")
        LogService.e("LoginServiceTest", "test")
        LogService.v("LoginServiceTest", "test")

        assertEquals(LogService.logs.size, 5)
    }

}