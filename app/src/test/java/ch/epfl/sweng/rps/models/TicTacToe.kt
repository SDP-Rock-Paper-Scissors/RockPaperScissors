package ch.epfl.sweng.rps.models

import ch.epfl.sweng.rps.models.remote.GameMode
import ch.epfl.sweng.rps.models.remote.Hand
import ch.epfl.sweng.rps.models.remote.Round
import com.google.firebase.Timestamp
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class TicTacToe {

    @Test
    fun `test ties`() {
        val now = Timestamp.now()
        // A tied round
        val round3 = Round.TicTacToe(
            listOf(
                1, 2, 1,
                1, 1, 2,
                2, 1, 2,
            ),
            mapOf("p1" to 1, "p2" to 2),
            "p2",
            now,
        )
        assertNull(round3.getWinner())
        val scores = round3.computeScores()
        assertTrue {
            scores.all { it.results == listOf(Hand.Outcome.TIE) }
        }
        assertEquals(2, scores.size)
    }

    @Test
    fun `test game not finished`() {
        val now = Timestamp.now()
        // A tied round
        val round3 = Round.TicTacToe(
            listOf(
                1, 2, 1,
                1, 1, 2,
                2, null, 2,
            ),
            mapOf("p1" to 1, "p2" to 2),
            "p2",
            now,
        )
        assertNull(round3.getWinner())
        val scores = round3.computeScores()
        assertEquals(0, scores.size)
    }


    @Test
    fun `test wins`() {
        val now = Timestamp.now()
        val r = Round.TicTacToe(
            listOf(
                null, 2, 1,
                null, 1, 2,
                1, null, null,
            ),
            mapOf("p1" to 1, "p2" to 2),
            "p1",
            now,
        )
        assertEquals("p1", r.getWinner())

        val scores = r.computeScores()

        assertEquals("p1", scores[0].uid)
        assertEquals(1, scores[0].points)
        assertEquals("p2", scores[1].uid)
        assertEquals(-1, scores[1].points)
    }

    @Test
    fun `test all null`() {
        val now = Timestamp.now()
        val round = Round.TicTacToe(
            (0 until 9).map { null },
            mapOf("p1" to 1, "p2" to 2),
            "p1",
            now,
        )
        assertNull(round.getWinner())
    }

    @Test
    fun `edition is right`() {
        val now = Timestamp.now()
        val round = Round.TicTacToe(
            listOf(
                1, 2, 1,
                1, 1, 2,
                2, 1, 2,
            ),
            mapOf("p1" to 1, "p2" to 2),
            "p2",
            now,
        )
        assertEquals(GameMode.GameEdition.TicTacToe, round.edition)
    }
}