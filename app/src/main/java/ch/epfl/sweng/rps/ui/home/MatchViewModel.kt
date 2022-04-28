package ch.epfl.sweng.rps.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.epfl.sweng.rps.models.ComputerPlayer
import ch.epfl.sweng.rps.models.Game
import ch.epfl.sweng.rps.models.Hand
import ch.epfl.sweng.rps.models.Round
import ch.epfl.sweng.rps.services.GameService
import ch.epfl.sweng.rps.services.OfflineGameService
import ch.epfl.sweng.rps.services.ServiceLocator
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import java.util.*

/**
 * Shared ViewModel among HomeFragment and GameFragment.
 * Sharing is needed due to the proper Game initialization and synchronization with GameFragment creation.
 */

class MatchViewModel : ViewModel() {

    var gameService: GameService? = null
    var currentRoundResult: Hand.Result? = null
    var gameResult: Hand.Result? = null
    var cumulativeScore = MutableLiveData<List<Round.Score>>()
    var computerPlayer: ComputerPlayer? = null
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val computerPlayerCurrentPoints: String
        get() =
            cumulativeScore.value?.filter { score ->
                score.uid == computerPlayer!!.computerPlayerId
            }?.get(0)?.points.toString()
    val userPlayerCurrentPoints: String
        get() = cumulativeScore.value?.filter { score -> score.uid == uid }
            ?.get(0)?.points.toString()

    fun startOfflineGameService(
        nEvents: Int,
        computerPlayer: ComputerPlayer,
        artificialMovesDelay: Long = 1_000
    ) {
        // TODO: when having a local DB change the random UUID
        this.computerPlayer = computerPlayer
        val gameId = UUID.randomUUID().toString()
        gameService = OfflineGameService(
            gameId,
            ServiceLocator.getInstance().repository,
            listOf(computerPlayer),
            Game.GameMode(2, Game.GameMode.Type.PC, nEvents, 0),
            artificialMovesDelay
        )
        gameService?.startListening()
    }

    private fun updateCumulativePoints() {
        cumulativeScore.value = if (cumulativeScore.value == null) {
            gameService?.currentRound!!.computeScores()
        } else {
            val newRoundScores = gameService?.currentRound!!.computeScores().sortedBy { it.uid }
            val updatedList = mutableListOf<Round.Score>()
            for ((old, new) in cumulativeScore.value!!.sortedBy { it.uid }.zip(newRoundScores)) {
                updatedList.add(Round.Score(old.uid, listOf(), old.points + new.points))
            }
            updatedList
        }
    }

    private fun determineResults(scores: List<Round.Score>): Hand.Result {
        val best = scores[0]
        val bestPoints = best.points
        val bestScoreList = scores.filter { it.points == bestPoints }
        val result = if (uid in bestScoreList.map { it.uid }) {
            if (bestScoreList.size == 1) {
                Hand.Result.WIN
            } else {
                Hand.Result.TIE
            }
        } else {
            Hand.Result.LOSS
        }
        return result;
    }

    private fun determineRoundResult() {
        val scores = gameService?.currentRound!!.computeScores()
        currentRoundResult = determineResults(scores)
    }

    private fun determineGameResult() {
        if (gameService?.isGameOver!!)
            gameResult = determineResults(cumulativeScore.value!!)
    }

    fun scoreBasedUpdates() {
        determineRoundResult()
        updateCumulativePoints()
        determineGameResult()
    }

    fun playHand(
        userHand: Hand,
        opponentsMoveUIUpdateCallback: () -> Unit,
        scoreBasedUpdatesCallback: () -> Unit,
        resultNavigationCallback: () -> Unit
    ) {
        viewModelScope.launch {
            ensureActive()
            gameService?.playHand(userHand)
            opponentsMoveUIUpdateCallback()
            scoreBasedUpdatesCallback()
            // the delay to let the user see the opponent's choice (rock/paper/scissors)
            // otherwise the transition is too fast to notice
            delay(1000L)
            resultNavigationCallback()
            // the delay to let the user see the result of the game (win/loss/draw)
            delay(1000L)
            // add round can be called only from suspend function or from coroutine
            // therefore  I play it here here
            if (!gameService?.isGameOver!!) {
                gameService?.addRound()
            }
        }
    }
}