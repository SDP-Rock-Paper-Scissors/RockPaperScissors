package ch.epfl.sweng.rps.services

import ch.epfl.sweng.rps.remote.Repository
import ch.epfl.sweng.rps.models.*
import ch.epfl.sweng.rps.models.remote.Game
import ch.epfl.sweng.rps.models.remote.GameMode
import ch.epfl.sweng.rps.models.remote.Hand
import ch.epfl.sweng.rps.models.remote.Round
import ch.epfl.sweng.rps.models.xbstract.ComputerPlayer
import com.google.firebase.Timestamp
import kotlinx.coroutines.delay

class OfflineGameService(
    override val gameId: String,
    private val repository: Repository,
    private val computerPlayers: List<ComputerPlayer>,
    private val gameMode: GameMode,
    @Suppress("UNUSED_PARAMETER")
    val artificialMovesDelay: Long = DEFAULT_DELAY
) : GameService() {

    companion object {
        private const val DEFAULT_DELAY = 100L
    }

    private var _disposed = false

    /**
     * Initialises the game after the game choice and play again button.
     * (play again currently not available)
     */


    override val isGameOver: Boolean
        get() {
            return game?.done ?: return false
        }


    override suspend fun addRound(): Round {
        checkNotDisposed()
        val round = Round.Rps(
            hands = mutableMapOf(),
            timestamp = Timestamp.now(),
        )
        game = (game!! as Game.Rps).copy(current_round = game!!.current_round.plus(1))
        setRound(round)
        return round
    }


    private fun setRound(round: Round) {
        (currentGame.rounds as MutableMap)[game!!.current_round.toString()] = round
    }

    private val currentHands get() = currentRound.hands as MutableMap

    override suspend fun playHand(hand: Hand) {
        checkNotDisposed()
        val me: String = repository.getCurrentUid()
        currentHands[me] = hand
        makeComputerMoves()
    }

    override suspend fun updateDone() {
        val isDone =
            roundCountBasedDone() && game!!.rounds[game!!.current_round.toString()]?.hands?.keys?.containsAll(
                game!!.players
            ) ?: false
        game = (game as Game.Rps).copy(done = isDone)
    }

    override fun roundCountBasedDone(): Boolean {
        return game?.current_round == game?.gameMode?.rounds!! - 1
    }

    private suspend fun makeComputerMoves() {
        for (pc in computerPlayers) {
            delay(artificialMovesDelay)
            currentHands[pc.uid] = pc.makeMove()
        }
    }

    override val currentGame: Game
        get() {
            checkNotDisposed()
            if (game == null) {
                throw error
                    ?: GameServiceException("Game service has not received a game yet")
            } else {
                return game!!
            }
        }

    override val currentRound: Round
        get() = game!!.rounds[game!!.current_round.toString()]!!

    override val isGameFull: Boolean
        get() = true


    override fun dispose() {
        checkNotDisposed()
        _disposed = true
        super.dispose()
    }

    override fun stopListening() {
    }

    override val isDisposed: Boolean
        get() = _disposed

    override val started: Boolean
        get() = game != null

    override suspend fun refreshGame(): Game = game!!

    override fun startListening(): GameService {
        checkNotDisposed()
        val round = Round.Rps(
            hands = mutableMapOf(),
            timestamp = Timestamp.now(),
        )
        game = Game.Rps(
            gameId,
            computerPlayers.map { it.uid },
            mutableMapOf("0" to round),
            0,
            gameMode.toGameModeString(),
            false,
            Timestamp.now(),
            gameMode.playerCount
        )
        return this
    }

    private fun checkNotDisposed() {
        if (_disposed) {
            throw GameServiceException("GameService is disposed")
        }
    }

    override val imTheOwner get() = true

    override suspend fun awaitForAllHands() {
        awaitFor { currentRound.hands.size == 2 }// 2 is the number of players, for now hardcoded
    }

    override suspend fun awaitForRoundAdded() {
        awaitFor { true }
    }
}