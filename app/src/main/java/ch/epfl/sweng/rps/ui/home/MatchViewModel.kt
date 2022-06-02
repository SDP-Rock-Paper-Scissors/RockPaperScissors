package ch.epfl.sweng.rps.ui.home

import android.app.Activity
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.epfl.sweng.rps.models.remote.GameMode
import ch.epfl.sweng.rps.models.remote.Hand
import ch.epfl.sweng.rps.models.remote.Round
import ch.epfl.sweng.rps.models.remote.User
import ch.epfl.sweng.rps.models.xbstract.AbstractUser
import ch.epfl.sweng.rps.models.xbstract.ComputerPlayer
import ch.epfl.sweng.rps.persistence.Cache
import ch.epfl.sweng.rps.services.FirebaseGameService
import ch.epfl.sweng.rps.services.GameService
import ch.epfl.sweng.rps.services.OfflineGameService
import ch.epfl.sweng.rps.services.ServiceLocator
import ch.epfl.sweng.rps.utils.showSnackbarIfError
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
    var currentRoundResult: Hand.Outcome? = null
    private var gameResult: Hand.Outcome? = null
    var cumulativeScore = MutableLiveData<List<Round.Score>?>()
    var cache = Cache.getInstance()

    var host: MutableLiveData<AbstractUser?> = MutableLiveData(null)
    var opponent: MutableLiveData<AbstractUser?> = MutableLiveData(User("opponent"))

    private var nEvents: Int? = null
    private var artificialMovesDelay: Long? = null
    var timeLimit: Int? = 0 // this will be modifiable when the options allow it
    var job: Job? = null
    var repository = ServiceLocator.getInstance().repository
    var uid: String = repository.getCurrentUid()

    val computerPlayerCurrentPoints: String
        get() =
            cumulativeScore.value?.filter { score ->
                score.uid == opponent.value!!.uid
            }?.get(0)?.points.toString()
    val userPlayerCurrentPoints: String
        get() = cumulativeScore.value?.filter { score -> score.uid == uid }
            ?.get(0)?.points.toString()

    init {
        viewModelScope.launch {
            cache.getUserDetails().then { host.value = it }.getOrThrow()
        }
    }

    fun setGameServiceSettings(
        nEvents: Int,
        opponent: AbstractUser,
        artificialMovesDelay: Long = 1_000
    ) {
        this.nEvents = nEvents
        this.opponent.value = opponent
        this.artificialMovesDelay = artificialMovesDelay
    }

    fun setGameServiceSettingsOnline(
        activity: Activity,
        gameService_: FirebaseGameService
    ) {
        gameService = gameService_
        val opponentUid =
            (gameService as FirebaseGameService).currentGame.players.filter { it != uid }[0]
        viewModelScope.launch {
            opponent.value = ServiceLocator.getInstance().repository.getUser(opponentUid)
                .showSnackbarIfError(activity).asData?.value
        }

        this.nEvents = (gameService as FirebaseGameService).currentGame.gameMode.rounds
    }

    fun startOfflineGameService() {
        // TODO: when having a local DB change the random UUID
        val gameId = UUID.randomUUID().toString()
        gameService = OfflineGameService(
            gameId,
            ServiceLocator.getInstance().repository,
            listOf(opponent.value!! as ComputerPlayer),
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

    private fun determineResults(scores: List<Round.Score>): Hand.Outcome {
        val best = scores[0]
        val bestPoints = best.points
        val bestScoreList = scores.filter { it.points == bestPoints }
        val result = if (uid in bestScoreList.map { it.uid }) {
            if (bestScoreList.size == 1) {
                Hand.Outcome.WIN
            } else {
                Hand.Outcome.TIE
            }
        } else {
            Hand.Outcome.LOSS
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
            opponent.value = User("opponent")
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
            Log.i("gameFlow", "in the launch")
//            gameService?.awaitForRoundAdded()
            while (true) {
                gameService?.refreshGame()
                delay(100L)
                if (gameService?.amITheHost == true || gameService?.currentRound?.hands?.size == 1) {
                    break
                }
            }
            Log.i("gameFlow", "round added awaiting done")
            gameService?.playHand(userHand)
            Log.i("gameFlow", "play hand in gameService done")
//            gameService?.awaitForAllHands()
            Log.i("gameFlow", gameService?.currentRound?.hands?.size.toString())
            while (true) {
                gameService?.refreshGame()
                delay(100L)
                Log.i("gameFlow", gameService?.currentRound?.hands?.size.toString())
                if (gameService?.currentRound?.hands?.size == 2) {
                    break
                }
            }
            Log.i("gameFlow", "await for all hands done")
            opponentsMoveUIUpdateCallback()
            scoreBasedUpdatesCallback()
            if (gameService?.imTheOwner == true) {
                gameService?.updateDone()
            }

            // add round can be called only from suspend function or from coroutine
            // therefore I use it here here
            if (!gameService?.isGameOver!! && gameService!!.amITheHost) {
                gameService?.addRound()
            }
            // the delay to let the user see the opponent's choice (rock/paper/scissors)
            // otherwise the transition is too fast to notice
            delay(1000L)
            while (true) {
                gameService?.refreshGame()
                delay(100L)
                if (gameService?.roundCountBasedDone() == gameService?.isGameOver) {
                    gameService?.stopListening()
                    break
                }
            }
            resultNavigationCallback()
            resetUIScoresCallback()
        }
    }


}