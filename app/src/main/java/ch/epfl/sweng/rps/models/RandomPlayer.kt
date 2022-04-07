package ch.epfl.sweng.rps.models

class RandomPlayer(private val availableHands: List<Hand>) :
    ComputerPlayer("randomPlayer") {
    override fun makeMove(): Hand {
        return availableHands.random()
    }
}