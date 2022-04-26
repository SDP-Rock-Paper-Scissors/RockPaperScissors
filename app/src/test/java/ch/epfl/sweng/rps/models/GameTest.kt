package ch.epfl.sweng.rps.models

import ch.epfl.sweng.rps.models.Game.GameMode
import com.google.firebase.Timestamp
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test


class GameTest {
    @Test
    fun testGameMode() {
        val m1 =
            GameMode(playerCount = 2, type = GameMode.Type.PVP, rounds = 3, timeLimit = 10)
        val m2 =
            GameMode(playerCount = 2, type = GameMode.Type.PVP, rounds = 3, timeLimit = 0)

        assertEquals(m1, GameMode.fromString(m1.toGameModeString()))
        assertEquals(m2, GameMode.fromString(m2.toGameModeString()))


        val g = Game(
            game_mode = m1.toGameModeString(),
            players = listOf("player1", "player2"),
            current_round = 0,
            done = false,
            timestamp = Timestamp.now(),
            player_count = 2,
            id = "id",
            rounds = mutableMapOf()
        )

        assertEquals(g.game_mode, m1.toGameModeString())
        val m3 = g.mode

        assertEquals(m1, m3)
    }
}