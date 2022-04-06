package ch.epfl.sweng.rps.models

import kotlin.random.Random

class RandomPlayer(numberOfPossibleMoves: Int) :
    ComputerPlayer("randomPlayer", numberOfPossibleMoves) {
    override fun makeMove(): Hand {
        val upperRange: Int = numberOfPossibleMoves - 1
        return when (Random.nextInt(0, upperRange)) {
            0 -> Hand.ROCK
            1 -> Hand.PAPER
            2 -> Hand.SCISSORS
            else -> throw IllegalStateException("Given number is not correct")
        }
    }
}