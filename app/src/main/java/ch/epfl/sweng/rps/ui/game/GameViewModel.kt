package ch.epfl.sweng.rps.ui.game

import androidx.lifecycle.ViewModel
import ch.epfl.sweng.rps.models.Hand
import ch.epfl.sweng.rps.models.RandomPlayer

class GameViewModel : ViewModel() {
    private var  computerChoice: Hand = RandomPlayer().makeMove(3)
    fun determineWinner(userChoice: Hand): Hand.Result {
        return userChoice vs computerChoice
    }
}