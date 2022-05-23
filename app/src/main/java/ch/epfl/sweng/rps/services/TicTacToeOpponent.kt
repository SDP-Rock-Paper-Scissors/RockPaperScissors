package ch.epfl.sweng.rps.services

interface TicTacToeOpponent {
    fun makeMove(): Pair<Int, Int>
}