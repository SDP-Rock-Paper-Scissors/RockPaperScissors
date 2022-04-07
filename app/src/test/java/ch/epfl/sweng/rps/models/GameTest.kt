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
        val m2 = GameMode.fromString(m1.toGameModeString())

        assertEquals(m1.playerCount, m2.playerCount)
        assertEquals(m1.rounds, m2.rounds)
        assertEquals(m1.type, m2.type)
        assertEquals(m1.timeLimit, m2.timeLimit)

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

        assertEquals(m1.playerCount, m3.playerCount)
        assertEquals(m1.rounds, m3.rounds)
        assertEquals(m1.type, m3.type)
        assertEquals(m1.timeLimit, m3.timeLimit)
    }
}