package ch.epfl.sweng.rps.models

import com.google.firebase.Timestamp
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals


class GameTest {
    @Test
    fun testTicTacToe() {
        /**
         * public constructor TicTacToe(
        id: String,
        players: List<String>,
        rounds: Map<String, Round.TicTacToe>,
        current_round: Int,
        game_mode: String,
        done: Boolean,
        timestamp: Timestamp,
        player_count: Int
        )
         */
        val now = Timestamp.now()
        val now2 = Timestamp.now()
        val game = Game.TicTacToe(
            "1",
            listOf("p1", "p2"),
            mapOf(
                "0" to Round.TicTacToe(
                    (0 until 9).map { 0 },
                    mapOf("p1" to 1, "p2" to 2),
                    "p1",
                    now,
                )
            ),
            0,
            "",
            false,
            now2,
            2
        )
        assertEquals("1", game.id)
        assertEquals(2, game.player_count)
        assertEquals(0, game.current_round)
        assertEquals(false, game.done)
        assertEquals("", game.game_mode)
        assertEquals(now2, game.timestamp)
        assertEquals(listOf("p1", "p2"), game.players)
        assertEquals(
            mapOf(
                "0" to Round.TicTacToe(
                    (0 until 9).map { 0 },
                    mapOf("p1" to 1, "p2" to 2),
                    "p1",
                    now,
                )
            ), game.rounds
        )
    }

}