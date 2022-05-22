package ch.epfl.sweng.rps.models

class RandomPlayer(private val availableHands: List<Hand>) :
    ComputerPlayer("Random Player", "randomPlayer") {
    override fun makeMove(): Hand {
        return availableHands.random()
    }
}