package ch.epfl.sweng.rps.models

import ch.epfl.sweng.rps.models.Hand.*
import ch.epfl.sweng.rps.models.Hand.Result.*
import org.junit.Assert.assertEquals
import org.junit.Test

class HandTest {
    @Test
    fun table() {
        assertEquals(WIN, ROCK vs SCISSORS)
        assertEquals(WIN, PAPER vs ROCK)
        assertEquals(WIN, SCISSORS vs PAPER)
    }

    @Test
    fun noneAlwaysLoses() {
        assertEquals(LOSE, NONE vs ROCK)
        assertEquals(LOSE, NONE vs PAPER)
        assertEquals(LOSE, NONE vs SCISSORS)

        assertEquals(WIN, ROCK vs NONE)
        assertEquals(WIN, PAPER vs NONE)
        assertEquals(WIN, SCISSORS vs NONE)

        assertEquals(DRAW, NONE vs NONE)
    }

    @Test
    fun isCommutative() {
        for (a in Hand.values()) {
            for (b in Hand.values()) {
                assertEquals(Hand.winner(a, b), Hand.winner(b, a))
            }
        }
    }

    // ignore for now because none makes it unbalanced
    // @Test
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