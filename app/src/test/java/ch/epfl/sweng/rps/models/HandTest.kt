package ch.epfl.sweng.rps.models

import ch.epfl.sweng.rps.models.Hand.*
import ch.epfl.sweng.rps.models.Hand.Result.WIN
import org.junit.Assert.assertEquals
import org.junit.Test

class HandTest {
    @Test
    fun table() {
        assertEquals(ROCK vs SCISSORS, WIN)
        assertEquals(SCISSORS vs PAPER, WIN)
        assertEquals(PAPER vs ROCK, WIN)
    }

    @Test
    fun isCommutative() {
        for (a in Hand.values()) {
            for (b in Hand.values()) {
                assertEquals(Hand.winner(a, b), Hand.winner(b, a))
            }
        }
    }

    @Test
    fun balanced() {
        // Check that every hand has the same amount of Wins, Losses and Eq vs other hands.
        val wins = Hand.values().associateWith { 0 }.toMutableMap()
        val eq = Hand.values().associateWith { 0 }.toMutableMap()
        val losses = Hand.values().associateWith { 0 }.toMutableMap()
        for (a in Hand.values()) {
            for (b in Hand.values()) {
                when (a) {
                    b -> {
                        eq.merge(a, 1, Int::plus)
                    }
                    Hand.winner(a, b) -> {
                        wins.merge(a, 1, Int::plus)
                    }
                    else -> {
                        losses.merge(a, 1, Int::plus)
                    }
                }
            }
        }
        assertEquals(wins.values.distinct().size, 1)
        assertEquals(losses.values.distinct().size, 1)
        assertEquals(eq.values.distinct().size, 1)
    }
}