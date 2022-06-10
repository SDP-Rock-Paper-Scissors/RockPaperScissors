package ch.epfl.sweng.rps.services

import ch.epfl.sweng.rps.ui.tictactoe.TicTacToeFragment
import kotlin.random.Random


class OfflineTicTacToe(val view: TicTacToeFragment, player: MOVES) : TicTacToeGame(player) {
    var player2 = object : TicTacToeOpponent {
        override fun makeMove(): Pair<Int, Int> {
            return Pair(Random.nextInt(0, 3), Random.nextInt(0, 3))
        }
    }

    override fun putChoice(square: Int) {
        if (!gameRunning)
            return
        val cell = square % 3
        val row = (square / 3)
        if (matrix[row][cell] != MOVES.EMPTY)
            return
        matrix[row][cell] = player
        view.updateUI(square, player)
        if (calculate()) return
        val opponent = if (player == MOVES.CROSS) MOVES.CIRCLE else MOVES.CROSS
        while (true) {
            var oppChoice = player2.makeMove()
            var cell = oppChoice.second
            var row = oppChoice.first
            if (matrix[row][cell] == MOVES.EMPTY) {
                matrix[row][cell] = opponent
                view.updateUI((row * 3) + cell, opponent)
                calculate()
                break
            }
        }
    }

    override fun gameOver(winner: MOVES) {
        gameRunning = false
        view.gameOver(winner)
    }
}