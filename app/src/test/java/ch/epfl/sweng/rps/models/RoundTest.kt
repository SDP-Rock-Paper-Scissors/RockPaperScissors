package ch.epfl.sweng.rps.models

import ch.epfl.sweng.rps.models.remote.Hand
import ch.epfl.sweng.rps.models.remote.Round
import ch.epfl.sweng.rps.models.xbstract.PointSystem
import com.google.firebase.Timestamp
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class RoundTest {
    @Test
    fun testProperties() {
        val now = Timestamp.now()
        val round = Round.Rps(
            hands = hashMapOf(
                "player1" to Hand.ROCK,
                "player2" to Hand.SCISSORS
            ),
            timestamp = now,
        )
        assertEquals(Hand.ROCK, round.hands["player1"])
        assertEquals(Hand.SCISSORS, round.hands["player2"])
        assertEquals(now, round.timestamp)
    }

    @Test
    fun testScores() {
        val now = Timestamp.now()
        val round = Round.Rps(
            hands = hashMapOf(
                "player1" to Hand.ROCK,
                "player2" to Hand.SCISSORS,
                "player3" to Hand.PAPER,
                "player4" to Hand.PAPER,
            ),
            timestamp = now,
        )

        // player 2       | 2 win, 1 loss, 0 tie = 1
        // player 3 and 4 | 1 win, 1 loss, 1 tie = 0
        // player 1       | 1 win, 2 loss, 0 tie = -1

        val scores = round.computeScores()
        assertEquals("player2", scores[0].uid)
        assertEquals(1, scores[0].points)

        assertTrue(scores[1].uid in listOf("player3", "player4"))
        assertTrue(scores[2].uid in listOf("player3", "player4"))
        assertNotEquals(scores[1].uid, scores[2].uid)
        assertEquals(scores[3].uid, "player1")
    }

    @Test
    fun testScoringSystem() {
        val pointSystem = PointSystem.DefaultPointSystem()

        assertEquals(1, pointSystem.getPoints(Hand.Outcome.WIN))
        assertEquals(-1, pointSystem.getPoints(Hand.Outcome.LOSS))
        assertEquals(0, pointSystem.getPoints(Hand.Outcome.TIE))
    }

    @Test
    fun testMore() {
        val round = Round.Rps(
            hands = hashMapOf(
                "player1" to Hand.ROCK,
                "player2" to Hand.SCISSORS,
                "player3" to Hand.PAPER,
                "player4" to Hand.PAPER,
            ),
            timestamp = Timestamp.now(),
        )
        val first = round.computeScores().first()
        assertEquals(1, first.points)
        assertEquals("player2", first.uid)
        assertEquals(
            listOf(
                Hand.Outcome.WIN,
                Hand.Outcome.WIN,
                Hand.Outcome.LOSS
            ).sortedBy { it.ordinal }, first.results.sortedBy { it.ordinal })

    }
}