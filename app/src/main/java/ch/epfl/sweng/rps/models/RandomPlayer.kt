package ch.epfl.sweng.rps.models

import kotlin.random.Random

class RandomPlayer: ComputerPlayer{
    override fun makeMove(numberOfMoves: Int): Hand {
        val upperRange: Int = numberOfMoves - 1
        return when(Random.nextInt(0, upperRange)){
            0 -> Hand.ROCK
            1 -> Hand.PAPER
            2 -> Hand.SCISSORS
            else -> throw IllegalStateException("Given number is not correct")
        }
    }
}