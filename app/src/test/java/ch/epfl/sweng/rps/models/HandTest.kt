package ch.epfl.sweng.rps.models

import org.junit.Assert.assertEquals
import org.junit.Test

class HandTest {
    @Test
    fun table() {
        assertEquals(Hand.ROCK vs Hand.SCISSORS, Hand.ROCK)
        assertEquals(Hand.SCISSORS vs Hand.PAPER, Hand.SCISSORS)
        assertEquals(Hand.PAPER vs Hand.ROCK, Hand.PAPER)
    }

    @Test
    fun isCommutative() {
        for (a in Hand.values()) {
            for (b in Hand.values()) {
                assertEquals(a vs b, b vs a)
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
                if (a == b) {
                    eq.merge(a, 1, Int::plus)
                } else if (a vs b == a) {
                    wins.merge(a, 1, Int::plus)
                } else {
                    losses.merge(a, 1, Int::plus)
                }
            }
        }
        assertEquals(wins.values.distinct().size, 1)
        assertEquals(losses.values.distinct().size, 1)
        assertEquals(eq.values.distinct().size, 1)
    }
}