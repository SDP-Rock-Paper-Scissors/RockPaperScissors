package ch.epfl.sweng.rps.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.epfl.sweng.rps.models.ComputerPlayer
import ch.epfl.sweng.rps.models.GameMode
import ch.epfl.sweng.rps.models.Hand
import ch.epfl.sweng.rps.models.Round
import ch.epfl.sweng.rps.services.GameService
import ch.epfl.sweng.rps.services.OfflineGameService
import ch.epfl.sweng.rps.services.ServiceLocator
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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
    var cumulativeScore = MutableLiveData<List<Round.Score>?>()
    var computerPlayer: ComputerPlayer? = null
    var nEvents: Int? = null
    var artificialMovesDelay: Long? = null
    var timeLimit: Int? = 0 // this will be modifiable when the options allow it
    var job: Job? = null
    var repository = ServiceLocator.getInstance().repository
    var uid: String = repository.getCurrentUid()
    val computerPlayerCurrentPoints: String
        get() =
            cumulativeScore.value?.filter { score ->
                score.uid == computerPlayer!!.computerPlayerId
            }?.get(0)?.points.toString()
    val userPlayerCurrentPoints: String
        get() = cumulativeScore.value?.filter { score -> score.uid == uid }
            ?.get(0)?.points.toString()

    fun setGameServiceSettings(
        nEvents: Int,
        computerPlayer: ComputerPlayer,
        artificialMovesDelay: Long = 1_000
    ) {
        this.nEvents = nEvents
        this.computerPlayer = computerPlayer
        this.artificialMovesDelay = artificialMovesDelay
    }

    fun startOfflineGameService() {
        // TODO: when having a local DB change the random UUID
        val gameId = UUID.randomUUID().toString()
        gameService = OfflineGameService(
            gameId,
            ServiceLocator.getInstance().repository,
            listOf(computerPlayer!!),
            GameMode(2, GameMode.Type.PC, nEvents!!, 0, GameMode.GameEdition.RockPaperScissors),
            artificialMovesDelay!!
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
        return result
    }

    private fun determineRoundResult() {
        val scores = gameService?.currentRound?.computeScores()
        currentRoundResult = determineResults(scores!!)
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

    private fun resetResults() {
        currentRoundResult = null
        gameResult = null
        cumulativeScore = MutableLiveData<List<Round.Score>?>()
    }

    /**
     * MatchViewModel is reusable. Old data needs to be wiped out before starting the previous one.
     */
    fun reInit() {
        if (gameService!!.isGameOver) {
            gameService = null
            computerPlayer = null
            resetResults()
        }
    }

    fun reInitToPlayAgain() {
        startOfflineGameService()
        resetResults()
    }

    fun managePlayHand(
        userHand: Hand,
        opponentsMoveUIUpdateCallback: () -> Unit,
        scoreBasedUpdatesCallback: () -> Unit,
        resultNavigationCallback: () -> Unit,
        resetUIScoresCallback: () -> Unit
    ) {
        job = viewModelScope.launch {
            gameService?.playHand(userHand)
            opponentsMoveUIUpdateCallback()
            scoreBasedUpdatesCallback()
            // add round can be called only from suspend function or from coroutine
            // therefore I use it here here
            if (!gameService?.isGameOver!!) {
                gameService?.addRound()
            }
            // the delay to let the user see the opponent's choice (rock/paper/scissors)
            // otherwise the transition is too fast to notice
            delay(1000L)
            resultNavigationCallback()
            resetUIScoresCallback()
        }
    }
}