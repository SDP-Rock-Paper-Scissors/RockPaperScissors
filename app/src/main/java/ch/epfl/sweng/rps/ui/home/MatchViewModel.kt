package ch.epfl.sweng.rps.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.epfl.sweng.rps.db.FirebaseRepository
import ch.epfl.sweng.rps.models.ComputerPlayer
import ch.epfl.sweng.rps.models.Game
import ch.epfl.sweng.rps.models.Hand
import ch.epfl.sweng.rps.services.OfflineGameService
import ch.epfl.sweng.rps.services.ServiceLocator
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import java.util.*

/**
 * Shared ViewModel among HomeFragment and GameFragment.
 * Sharing is needed due to the proper Game initialization and synchronization with GameFragment creation.
 */

class MatchViewModel : ViewModel() {


    private var _gameService: OfflineGameService? = null
    var currentUserResult: Hand.Result? = null
    private val uid = FirebaseAuth.getInstance().currentUser?.uid
    fun startOfflineGameService(nEvents: Int, computerPlayer: ComputerPlayer) {
        val gameId = UUID.randomUUID().toString()
        _gameService = OfflineGameService(
            gameId,
            ServiceLocator.getInstance().repository as FirebaseRepository,
            listOf(computerPlayer),
            Game.GameMode(2, Game.GameMode.Type.PC, nEvents, 0)
        )
        _gameService?.startListening()
    }


    private fun determineResult() {
        val scores = _gameService?.currentRound!!.computeScores()
        val best = scores[0]
        val bestPoints = best.points
        val bestUid = best.uid
        currentUserResult = if (uid == bestUid) {
            val secondBestPoints = scores[1].points
            if (bestPoints > secondBestPoints) {
                Hand.Result.WIN
            } else {
                Hand.Result.LOSS
            }
        } else {
            Hand.Result.TIE
        }
    }

    fun playHand(userHand: Hand, callback: () -> Unit) {
        viewModelScope.launch {
            //add proper round adding when supporting the multiround
            ensureActive()
            _gameService?.playHand(userHand)
            determineResult()
            callback()

        }
    }
}