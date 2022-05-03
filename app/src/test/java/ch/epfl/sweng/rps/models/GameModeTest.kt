package ch.epfl.sweng.rps.models

import com.google.firebase.Timestamp
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

internal class GameModeTest {

    @Test
    fun `GameEdition ids must be unique`() {
        assertTrue {
            GameMode.GameEdition.values().map { it.id }
                .distinct().size == GameMode.GameEdition.values().size
        }
    }


    @Test
    fun testGameMode() {
        val m1 =
            GameMode(
                playerCount = 2,
                type = GameMode.Type.PVP,
                rounds = 3,
                timeLimit = 10,
                game = GameMode.GameEdition.RockPaperScissors,
            )
        val m2 =
            GameMode(
                playerCount = 2,
                type = GameMode.Type.PVP,
                rounds = 3,
                timeLimit = 0,
                GameMode.GameEdition.RockPaperScissors
            )

        assertEquals(m1, GameMode.fromString(m1.toGameModeString()))
        assertEquals(m2, GameMode.fromString(m2.toGameModeString()))


        val g = Game.Rps(
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
        val m3 = g.gameMode

        assertEquals(m1, m3)
    }

    @Test
    fun `test all game editions`() {
        val m1 =
            GameMode(
                playerCount = 2,
                type = GameMode.Type.PVP,
                rounds = 3,
                timeLimit = 10,
                game = GameMode.GameEdition.RockPaperScissors,
            )
        GameMode.GameEdition.values().forEach {
            assertEquals(it, GameMode.fromString(m1.copy(game = it).toGameModeString()).game)
        }
    }

    @Test
    fun `assert properties letters are sorted`() {
        forAll {
            val unsorted = it.toGameModeString().split(",")
            val sorted = unsorted.sorted()
            for (i in sorted.indices) {
                assertEquals(sorted[i], unsorted[i])
            }

        }
    }

    @Test
    fun `never contains a space`() {
        testThatForAll {
            it.toGameModeString().contains(" ").not()
        }
    }


    private fun testThatForAll(fn: (mode: GameMode) -> Boolean) {
        GameMode.Type.values().forEach {
            val mode = GameMode(
                playerCount = 2,
                type = it,
                rounds = 3,
                timeLimit = 10,
                game = GameMode.GameEdition.RockPaperScissors,
            )
            assertTrue {
                fn(mode)
            }
        }

        GameMode.GameEdition.values().forEach {
            val mode = GameMode(
                playerCount = 2,
                type = GameMode.Type.PVP,
                rounds = 3,
                timeLimit = 10,
                game = it,
            )
            assertTrue {
                fn(mode)
            }
        }
    }


    private fun forAll(fn: (mode: GameMode) -> Unit) {
        GameMode.Type.values().forEach {
            val mode = GameMode(
                playerCount = 2,
                type = it,
                rounds = 3,
                timeLimit = 10,
                game = GameMode.GameEdition.RockPaperScissors,
            )
            fn(mode)
        }

        GameMode.GameEdition.values().forEach {
            val mode = GameMode(
                playerCount = 2,
                type = GameMode.Type.PVP,
                rounds = 3,
                timeLimit = 10,
                game = it,
            )
            fn(mode)
        }
    }
}
