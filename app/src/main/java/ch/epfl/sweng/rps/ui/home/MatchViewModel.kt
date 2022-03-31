package ch.epfl.sweng.rps.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ch.epfl.sweng.rps.models.*

/**
 * Shared ViewModel among HomeFragment and GameFragment.
 * Sharing is needed due to the proper Game initialization and synchronization with GameFragment creation.
 */

class MatchViewModel : ViewModel() {
    private var _game: Game? = null
    val game: Game?
        get() = _game

    //later there should be a choice of a model
    private var _computer: ComputerPlayer = RandomPlayer()
    private var _computerCurrentHand: Hand = Hand.SCISSORS

    /**
     * param nEvents: number of Wins or Rounds depending on the game implementation
     */
    fun createGame(nEvents: Int) {
        //TODO: number of players to change when many computers allowed
        //TODO: user and game id have to be changed
        _game = Game(
            "toChangeGameId",
            "toChangeUserUid",
            listOf(Game.Uid("computerNamesToFigureOut", isComputer = true), Game.Uid("me", isComputer = false)),
            Game.Mode(2, Game.Mode.Type.PC, -1, nEvents),
            null
        )
        // if variants of RPS create makeMove here needs change
        // currently hardcoded 3 (rock, paper, scissors)
        _computerCurrentHand = _computer.makeMove(3)
    }

    fun determineRoundResults(roundId: String, uid: String, userHand: Hand): Hand.Result {
        addRound(roundId, uid, userHand)
        game?.roundsList?.last()?.computeScores()
        val winnerId = game?.roundsList?.last()?.winnerId
        _computerCurrentHand = _computer.makeMove(3)
        return when {
            game?.uid == winnerId -> {
                Hand.Result.WIN
            }
            winnerId == "" -> {
                Hand.Result.DRAW
            }
            else -> {
                Hand.Result.LOSE
            }
        }
    }
    private fun addRound(roundId: String, uid: String, userHand: Hand) {
        val opponentsHands: Map<String, Hand> = mapOf("computerNamesToFigureOut" to _computerCurrentHand)
        game?.addRound(uid, userHand, opponentsHands)

    }
}