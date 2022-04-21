package ch.epfl.sweng.rps.services

import ch.epfl.sweng.rps.logic.Repository
import ch.epfl.sweng.rps.models.ComputerPlayer
import ch.epfl.sweng.rps.models.Game
import ch.epfl.sweng.rps.models.Hand
import ch.epfl.sweng.rps.models.RandomPlayer
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import java.util.*

class OfflineGameServiceTest {
    private val gameId = UUID.randomUUID().toString()
    private var gameService: OfflineGameService? = null
    private val computerPlayers: List<ComputerPlayer> =
        listOf(RandomPlayer(listOf(Hand.PAPER, Hand.ROCK, Hand.SCISSORS)))

    private fun initGameService(nEvents: Int) {
        val repo = mock<Repository> {
            on { rawCurrentUid() } doReturn "testUserId"
        }

        gameService = OfflineGameService(
            gameId,
            repo,
            computerPlayers,
            Game.GameMode(2, Game.GameMode.Type.PC, nEvents, 0),
        )
        gameService?.startListening()
    }

    @Test
    fun `single round game is over`() {
        initGameService(1)
        runBlocking {
            gameService?.playHand(Hand.SCISSORS)

        }
        assertThat(gameService?.isGameOver, `is`(true))
    }

    @Test
    fun `single round game is not over`() {
        initGameService(1)
        // not over yet because a player haven't played (and computers neither)
        assertThat(gameService?.isGameOver, `is`(false))
    }

    @Test
    fun `two round game is over`() {
        initGameService(2)
        runBlocking {
            gameService?.playHand(Hand.SCISSORS)
            gameService?.addRound()
            gameService?.playHand(Hand.SCISSORS)
        }
        assertThat(gameService?.isGameOver, `is`(true))
    }

    @Test
    fun `game is disposed`() {
        initGameService(2)
        gameService?.dispose()
        assertThat(gameService?.isDisposed, `is`(true))
    }

    @Test
    fun `actions on disposed game impossible`() {
        initGameService(2)
        gameService?.dispose()
        assertThrows<GameService.GameServiceException> {
            runBlocking {
                gameService?.playHand(Hand.SCISSORS)
            }
        }
    }

    @Test
    fun `game always full`() {
        initGameService(1)
        assertThat(gameService?.isGameFull, `is`(true))
    }

}