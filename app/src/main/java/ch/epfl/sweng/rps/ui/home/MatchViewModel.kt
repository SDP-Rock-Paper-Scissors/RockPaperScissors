package ch.epfl.sweng.rps.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.epfl.sweng.rps.models.ComputerPlayer
import ch.epfl.sweng.rps.models.Hand
import ch.epfl.sweng.rps.services.OfflineGameService
import kotlinx.coroutines.launch

/**
 * Shared ViewModel among HomeFragment and GameFragment.
 * Sharing is needed due to the proper Game initialization and synchronization with GameFragment creation.
 */

class MatchViewModel : ViewModel() {


    private var _gameService: OfflineGameService? = null
    var currentUserResult: Hand.Result? = null

    fun startOfflineGameService(nEvents: Int, computerPlayer: ComputerPlayer) {
        //better way to get uid is needed
        _gameService = OfflineGameService("0111", listOf(computerPlayer), nEvents)
    }

    /**
     * param nEvents: number of Wins or Rounds depending on the game implementation
     */
    fun createGame() {
        _gameService?.createGame()

    }

    fun playHand(userHand: Hand) {
        viewModelScope.launch {
            _gameService?.addRound()
            _gameService?.playHand(userHand)
            currentUserResult = ??
        }
    }
}