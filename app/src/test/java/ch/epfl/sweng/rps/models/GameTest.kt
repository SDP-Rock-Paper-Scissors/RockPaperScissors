package ch.epfl.sweng.rps.models

import ch.epfl.sweng.rps.models.remote.Game
import ch.epfl.sweng.rps.models.remote.GameMode
import ch.epfl.sweng.rps.models.remote.Round
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
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
            "1", listOf("p1", "p2"), mapOf(
                "0" to Round.TicTacToe(
                    (0 until 9).map { 0 },
                    mapOf("p1" to 1, "p2" to 2),
                    "p1",
                    now,
                )
            ), 0, "", false, now2, 2
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

    /*
* data class Rps(
override val id: String = "",
override val players: List<String> = listOf(),
override val rounds: Map<String, Round.Rps> = mapOf(),
override val current_round: Int = 0,
override val game_mode: String = "",
override val done: Boolean = false,
override val timestamp: Timestamp = Timestamp(Date(0)),
override val player_count: Int = 0,
override val started: Boolean = false,
) : Game() {
override val edition: GameEdition = GameEdition.RockPaperScissors
}*/
    data class Cfg(val edition: Boolean, val gameMode: String?)

    @Test
    fun `create a game from document snapshot`() {

        val gm = GameMode(
            playerCount = 2,
            edition = GameMode.GameEdition.TicTacToe,
            rounds = 2,
            timeLimit = 0,
            type = GameMode.Type.LOCAL
        )
        val game = Game.TicTacToe(
            "1", listOf("p1", "p2"), mapOf(
                "0" to Round.TicTacToe(
                    (0 until 9).map { 0 },
                    mapOf("p1" to 1, "p2" to 2),
                    "p1",
                    Timestamp.now(),
                )
            ), 0, gm.toGameModeString(), false, Timestamp.now(), 2, true
        )

        assertEquals(game, mockGame(Cfg(true, gm.toGameModeString()), game))
        assertEquals(game, mockGame(Cfg(false, gm.toGameModeString()), game))
        assertThrows<Exception> { mockGame(Cfg(false, "a"), game) }
        assertThrows<IllegalArgumentException> { mockGame(Cfg(false, null), game) }
    }

    private fun mockGame(
        cfg: Cfg, game: Game.TicTacToe
    ): Game? {
        val doc = mockk<DocumentSnapshot>()

        every { doc.get(Game.FIELDS.EDITION) } returns if (cfg.edition) game.edition.name else null
        every { doc.get(Game.FIELDS.ID) } returns game.id
        every { doc.get(Game.FIELDS.PLAYERS) } returns game.players
        every { doc.get(Game.FIELDS.ROUNDS) } returns game.rounds
        every { doc.get(Game.FIELDS.CURRENT_ROUND) } returns game.current_round
        every { doc.get(Game.FIELDS.GAME_MODE) } returns cfg.gameMode
        every { doc.get(Game.FIELDS.DONE) } returns game.done
        every { doc.get(Game.FIELDS.TIMESTAMP) } returns game.timestamp
        every { doc.get(Game.FIELDS.PLAYER_COUNT) } returns game.player_count
        every { doc.get(Game.FIELDS.STARTED) } returns game.started

        @Suppress("UNCHECKED_CAST") every { doc.toObject(Game.TicTacToe::class.java) } answers {
            Game.TicTacToe(
                id = doc.get(Game.FIELDS.ID) as String,
                players = doc.get(Game.FIELDS.PLAYERS) as List<String>,
                rounds = doc.get(Game.FIELDS.ROUNDS) as Map<String, Round.TicTacToe>,
                current_round = doc.get(Game.FIELDS.CURRENT_ROUND) as Int,
                game_mode = doc.get(Game.FIELDS.GAME_MODE) as String,
                done = doc.get(Game.FIELDS.DONE) as Boolean,
                timestamp = doc.get(Game.FIELDS.TIMESTAMP) as Timestamp,
                player_count = doc.get(Game.FIELDS.PLAYER_COUNT) as Int,
                started = doc.get(Game.FIELDS.STARTED) as Boolean
            )
        }

        return Game.fromDocumentSnapshot(doc)
    }

}