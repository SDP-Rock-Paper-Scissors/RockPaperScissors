package ch.epfl.sweng.rps.models

import com.google.firebase.Timestamp
import org.junit.Assert.*
import org.junit.Test

class RoundTest {

    @Test
    fun testProperties() {
        val now = Timestamp.now()
        val round = Round(
            hands = hashMapOf(
                "player1" to Hand.ROCK,
                "player2" to Hand.SCISSORS
            ),
            timestamp = now,
            id = "uid",
            game_id = "game_id"
        )
        assertEquals(Hand.ROCK, round.hands["player1"])
        assertEquals(Hand.SCISSORS, round.hands["player2"])
        assertEquals(now, round.timestamp)
    }

    @Test
    fun testScores() {
        val now = Timestamp.now()
        val round = Round(
            hands = hashMapOf(
                "player1" to Hand.ROCK,
                "player2" to Hand.SCISSORS,
                "player3" to Hand.PAPER,
                "player4" to Hand.PAPER,
            ),
            timestamp = now,
            id = "uid",
            game_id = "game_id"
        )

        // player 2       | 2 win, 1 loss, 0 tie = 1
        // player 3 and 4 | 1 win, 1 loss, 1 tie = 0
        // player 1       | 1 win, 2 loss, 0 tie = -1

        val scores = round.computeScores()
        assertEquals("player2", scores[0].uid)
        assertEquals(1, scores[0].score)

        assertTrue(scores[1].uid in listOf("player3", "player4"))
        assertTrue(scores[2].uid in listOf("player3", "player4"))
        assertNotEquals(scores[1].uid, scores[2].uid)
        assertEquals(scores[3].uid, "player1")
    }
}