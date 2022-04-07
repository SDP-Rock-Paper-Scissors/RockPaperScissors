package ch.epfl.sweng.rps.services

import ch.epfl.sweng.rps.models.ComputerPlayer
import ch.epfl.sweng.rps.models.Game
import ch.epfl.sweng.rps.models.Hand
import ch.epfl.sweng.rps.models.Round
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestoreException
import java.util.*

class OfflineGameService(
    private val gameId_: String,
    private val uid: String,
    private val computerPlayers: List<ComputerPlayer>,
    private var roundCount: Int
) : GameService {

    private var _game: Game? = null


    /**
     * Initialises the game after the game choice and play again button.
     * (play again currently not available)
     */
    fun createGame() {
        // gameMode is ignored
        // done in not clear
        // player count hardcoded, no support for different modes now
        _game = Game(
            gameId_,
            computerPlayers.map { it.computerPlayerId },
            mutableMapOf(),
            0,
            "",
            false,
            Timestamp(Date()),
            2
        )
    }

    //TODO: change when support with db is added
    override val gameId: String
        get() = _game!!.id

    override val isGameOver: Boolean
        get() = _game?.current_round == roundCount


    override suspend fun addRound(): Round {
        val round = Round(
            hands = mutableMapOf(),
            timestamp = Timestamp.now(),
        )
        _game?.current_round = _game?.current_round?.plus(1)!!
        _game!!.rounds[_game?.current_round.toString()] = round
        return round

    }

    override suspend fun playHand(hand: Hand) {
        currentGame.rounds[currentGame.current_round.toString()]!!.hands[uid] = hand
        makeComputerMoves()

    }

    private fun makeComputerMoves() {
        for (pc in computerPlayers) {
            currentGame.rounds[currentGame.current_round.toString()]!!.hands[pc.computerPlayerId] =
                pc.makeMove()
        }
    }

    override val currentGame: Game
        get() = _game!!

    override val currentRound: Round
        get() = _game!!.rounds[_game!!.current_round.toString()]!!

    override val isGameFull: Boolean
        get() = true


    override fun dispose() {
    }

    override fun stopListening() {
    }

    override val isDisposed: Boolean
        get() = true

    override val active: Boolean
        get() = true

    override suspend fun refreshGame(): Game {
        return _game!!
    }

    override val error: FirebaseFirestoreException
        get() = TODO("Not yet implemented")

    override fun startListening(): GameService {
        return this
    }
}