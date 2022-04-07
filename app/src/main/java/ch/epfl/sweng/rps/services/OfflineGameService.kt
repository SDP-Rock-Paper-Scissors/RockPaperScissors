package ch.epfl.sweng.rps.services

import ch.epfl.sweng.rps.db.FirebaseRepository
import ch.epfl.sweng.rps.models.ComputerPlayer
import ch.epfl.sweng.rps.models.Game
import ch.epfl.sweng.rps.models.Hand
import ch.epfl.sweng.rps.models.Round
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.delay
import java.util.*

class OfflineGameService(
    override val gameId: String,
    private val firebaseRepository: FirebaseRepository,
    private val computerPlayers: List<ComputerPlayer>,
    private val gameMode: Game.GameMode
) : GameService {

    private var _game: Game? = null
    private var _disposed = false
    private val roundCount = gameMode.rounds
    private var _active = false
    private var _error: FirebaseFirestoreException? = null


    /**
     * Initialises the game after the game choice and play again button.
     * (play again currently not available)
     */


    override val isGameOver: Boolean
        get() = _game?.current_round == roundCount


    override suspend fun addRound(): Round {
        val round = Round(
            hands = mutableMapOf(),
            timestamp = Timestamp.now(),
        )
        (_game!!.rounds as MutableMap)[_game?.current_round.toString()] = round
        _game = _game?.copy(current_round = _game?.current_round?.plus(1)!!)
        return round

    }

    override suspend fun playHand(hand: Hand) {
        val me = firebaseRepository.getCurrentUid()
        ((currentGame.rounds as MutableMap)[currentGame.current_round.toString()]!!.hands as MutableMap)[me] =
            hand
        makeComputerMoves()

    }

    private suspend fun makeComputerMoves() {
        for (pc in computerPlayers) {
//                uncomment when the proper tests with coroutines implemented
//            delay(1_000)
            (currentGame.rounds[currentGame.current_round.toString()]!!.hands as MutableMap)[pc.computerPlayerId] =
                pc.makeMove()
        }
    }

    override val currentGame: Game
        get() {
            checkNotDisposed()
            if (_game == null) {
                throw error ?: GameService.GameServiceException("Game service has not received a game yet")
            } else {
                return _game!!
            }
        }

    override val currentRound: Round
        get() = _game!!.rounds[_game!!.current_round.toString()]!!

    override val isGameFull: Boolean
        get() = true


    override fun dispose() {
        checkNotDisposed()
        // TODO: probably here I should store it later
        _disposed = true
    }

    override fun stopListening() {
    }

    override val isDisposed: Boolean
        get() = _disposed

    override val active: Boolean
        get() = _game != null

    override suspend fun refreshGame(): Game {
        return _game!!
    }

    override val error: FirebaseFirestoreException?
        get() = _error

    override fun startListening(): GameService {
        checkNotDisposed()
        val round = Round(
            hands = mutableMapOf(),
            timestamp = Timestamp.now(),
        )
        _game = Game(
            gameId,
            computerPlayers.map { it.computerPlayerId },
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
            throw GameService.GameServiceException("GameService is disposed")
        }
    }
}