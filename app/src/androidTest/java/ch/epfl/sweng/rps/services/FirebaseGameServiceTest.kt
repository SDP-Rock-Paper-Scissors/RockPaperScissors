package ch.epfl.sweng.rps.services

import androidx.test.platform.app.InstrumentationRegistry
import ch.epfl.sweng.rps.TestUtils.initializeForTest
import ch.epfl.sweng.rps.db.FirebaseReferences
import ch.epfl.sweng.rps.db.FirebaseRepository
import ch.epfl.sweng.rps.models.Game
import ch.epfl.sweng.rps.models.Hand
import ch.epfl.sweng.rps.models.Round
import ch.epfl.sweng.rps.utils.consume
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.util.*
import kotlin.time.Duration.Companion.minutes

class FirebaseGameServiceTest {

    @Before
    fun setUp() {
        Firebase.initializeForTest()
        Firebase.auth.signOut()
    }

    @Test
    fun test() {
        val gameService = FirebaseGameService(
            FirebaseReferences(),
            FirebaseRepository.createInstance(FirebaseReferences()),
            "game_id"
        )
        assertEquals(false, gameService.active)
        assertThrows(GameService.GameServiceException::class.java) {
            gameService.currentGame
        }
        assertThrows(GameService.GameServiceException::class.java) {
            gameService.currentRound
        }
        val round1 = Round(
            mapOf(
                "player1" to Hand.SCISSORS,
                "player2" to Hand.PAPER
            ),
            Timestamp(Date(1))
        )
        val game = Game(
            "game1",
            listOf("player1", "player2"),
            mapOf(
                "0" to Round(
                    mapOf(
                        "player1" to Hand.ROCK,
                        "player2" to Hand.SCISSORS
                    ),
                    Timestamp(Date(0))
                ),
                "1" to round1
            ),
            1,
            Game.GameMode(
                2,
                Game.GameMode.Type.PVP,
                3,
                10
            ).toGameModeString(),
            true,
            Timestamp(Date(0)),
            2
        )
        gameService.setGameTest(game)
        assertEquals(game, gameService.currentGame)
        assertEquals(round1, gameService.currentRound)
        assertEquals(true, gameService.isGameFull)
        var called = 0
        val listener = consume { called++ }
        gameService.addErrorListener(listener)
        assertEquals(null, gameService.error)

        runBlocking {
            gameService.startListening()
            withTimeout(2.minutes) {
                gameService.awaitFor {
                    it.error != null
                }
            }
        }
        assertTrue(gameService.error is Exception)
        gameService.removeErrorListener(listener)
        assertEquals(1, called)
    }
}