package ch.epfl.sweng.rps.services

import ch.epfl.sweng.rps.models.ComputerPlayer
import ch.epfl.sweng.rps.models.Game
import ch.epfl.sweng.rps.models.Hand
import ch.epfl.sweng.rps.models.Round
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestoreException
import java.util.*

class OfflineGameService(
    private val uid: String,
    private val computerPlayer: ComputerPlayer,
    private var roundCount: Int
) : GameService {

    private var _game: Game? = null
    private var _round: Round? = null
    private var computerCurrentHand: Hand? = null
    private var userCurrentHand: Hand? = null

    /**
     * Initialises the game after the game choice and play again button.
     * (play again currently not available)
     */
    fun createGame() {
        // gameMode is ignored
        // done in not clear
        // player count hardcoded, no support for different modes now
        _game = Game(
            uid,
            listOf(computerPlayer.computerPlayerId),
            mutableMapOf(),
            1,
            "",
            false,
            Timestamp(Date()),
            2
        )
    }

    //TODO: change when support with db is added
    override val gameId: String
        get() = _game!!.current_round.toString()

    override val isGameOver: Boolean
        get() = _game?.current_round == roundCount


    suspend fun determineRoundResult(userHand: Hand): Hand.Result {
        userCurrentHand = userHand
        computerCurrentHand = computerPlayer.makeMove()
        _round = addRound()
        val scoreList = _round!!.computeScores()
        var currentUserResult: Hand.Result? = null
        for (scoreRes in scoreList) {
            if (scoreRes.uid == uid) {
                currentUserResult = scoreRes.results[0]
                break
            }
        }
        return currentUserResult!!
    }

    override suspend fun addRound(): Round {
        return Round(
            mapOf(
                uid to userCurrentHand!!,
                computerPlayer.computerPlayerId to computerCurrentHand!!
            ), Timestamp(Date())
        )
    }

    override val currentGame: Game
        get() = _game!!

    override val currentRound: Round
        get() = _round!!

    override val isGameFull: Boolean
        get() = true

    override suspend fun playHand(hand: Hand) {
        TODO("Not yet implemented")
    }

    override fun dispose() {
        TODO("Not yet implemented")
    }

    override val isDisposed: Boolean
        get() = TODO("Not yet implemented")

    override val active: Boolean
        get() = TODO("Not yet implemented")

    override suspend fun refreshGame(): Game {
        TODO("Not yet implemented")
    }

    override val error: FirebaseFirestoreException
        get() = TODO("Not yet implemented")

    override fun startListening(): GameService {
        TODO("probably not needed here")
    }

    override fun stopListening() {
        TODO("Not yet implemented")
    }
}