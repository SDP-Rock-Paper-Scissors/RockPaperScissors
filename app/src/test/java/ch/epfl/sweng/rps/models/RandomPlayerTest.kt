package ch.epfl.sweng.rps.models

import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers.hasItem
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test


class RandomPlayerTest {

    @Test
    fun testRandomFromOneItem() {
        val pc = RandomPlayer(listOf(Hand.SCISSORS))
        val move = pc.makeMove()
        assertEquals(move, Hand.SCISSORS)
    }

    @Test
    fun testRandomChoiceInRange() {
        val choices = listOf(Hand.SCISSORS, Hand.PAPER, Hand.ROCK)
        val pc = RandomPlayer(choices)
        val move = pc.makeMove()
        MatcherAssert.assertThat(choices, hasItem(move))
    }
}