package ch.epfl.sweng.rps.models

import org.junit.Assert.assertEquals
import org.junit.jupiter.api.Test

class UserStatsTest {
    @Test
    fun test() {
        val userStats = UserStats()
        assertEquals(0, userStats.wins)
        assertEquals(0, userStats.total_games)
        assertEquals("", userStats.userId)

        assertEquals(.5, UserStats("", 1, 2).winRate, 0.01)
    }
}