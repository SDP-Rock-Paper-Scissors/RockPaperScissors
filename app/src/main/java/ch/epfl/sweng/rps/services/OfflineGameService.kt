package ch.epfl.sweng.rps.services

import ch.epfl.sweng.rps.db.Repository
import ch.epfl.sweng.rps.models.*
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
            val game = game ?: return false
            return game.current_round == game.gameMode.rounds - 1 && game.rounds[game.current_round.toString()]?.hands?.keys?.containsAll(
                game.players
            ) ?: false
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
}