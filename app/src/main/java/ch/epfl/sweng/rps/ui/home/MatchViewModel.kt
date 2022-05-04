package ch.epfl.sweng.rps.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.epfl.sweng.rps.models.ComputerPlayer
import ch.epfl.sweng.rps.models.GameMode
import ch.epfl.sweng.rps.models.Hand
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
    var currentUserResult: Hand.Result? = null
    var computerPlayer: ComputerPlayer? = null
    private val uid = FirebaseAuth.getInstance().currentUser?.uid
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
            GameMode(2, GameMode.Type.PC, nEvents, 0, GameMode.GameEdition.RockPaperScissors),
            artificialMovesDelay
        )
        gameService?.startListening()
    }


    private fun determineResult() {
        val scores = gameService?.currentRound!!.computeScores()
        val best = scores[0]
        val bestPoints = best.points
        val bestScoreList = scores.filter { it.points == bestPoints }
        currentUserResult = if (uid in bestScoreList.map { it.uid }) {
            if (bestScoreList.size == 1) {
                Hand.Result.WIN
            } else {
                Hand.Result.TIE
            }
        } else {
            Hand.Result.LOSS
        }
    }

    fun playHand(
        userHand: Hand,
        updateUIResultCallback: () -> Unit,
        resultNavigationCallback: () -> Unit,
        isGameOverCallback: () -> Unit
    ) {
        viewModelScope.launch {
            ensureActive()
            gameService?.playHand(userHand)
            determineResult()
            updateUIResultCallback()
            // the delay to let the user see the opponent's choice (rock/paper/scissors)
            // otherwise the transition is too fast to notice
            delay(1000L)
            resultNavigationCallback()
            // the delay to let the user see the result of the game (win/loss/draw)
            delay(1000L)
            isGameOverCallback()
        }
    }
}