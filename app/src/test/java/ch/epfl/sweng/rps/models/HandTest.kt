package ch.epfl.sweng.rps.models

import ch.epfl.sweng.rps.models.Hand.*
import ch.epfl.sweng.rps.models.Hand.Result.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class HandTest {

    @Test
    fun `assert in match-up at one loses`() {
        Hand.checkEveryHandLosesTo()
    }

    @Test
    fun `make sure the basic wins`() {
        assertEquals(WIN, ROCK vs SCISSORS)
        assertEquals(WIN, PAPER vs ROCK)
        assertEquals(WIN, SCISSORS vs PAPER)
    }

    @Test
    fun `none always loses`() {
        assertEquals(LOSS, NONE vs ROCK)
        assertEquals(LOSS, NONE vs PAPER)
        assertEquals(LOSS, NONE vs SCISSORS)

        assertEquals(WIN, ROCK vs NONE)
        assertEquals(WIN, PAPER vs NONE)
        assertEquals(WIN, SCISSORS vs NONE)

        assertEquals(TIE, NONE vs NONE)
    }

    @Test
    fun `check winner is commutative`() {
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