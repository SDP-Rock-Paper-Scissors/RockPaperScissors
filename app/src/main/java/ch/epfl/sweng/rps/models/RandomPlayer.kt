package ch.epfl.sweng.rps.models

import ch.epfl.sweng.rps.models.remote.Hand
import ch.epfl.sweng.rps.models.xbstract.ComputerPlayer

class RandomPlayer(private val availableHands: List<Hand>) :
    ComputerPlayer("Random Player", "randomPlayer") {
    override fun makeMove(): Hand {
        return availableHands.random()
    }
}