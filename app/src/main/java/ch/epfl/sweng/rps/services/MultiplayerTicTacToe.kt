package ch.epfl.sweng.rps.services

import ch.epfl.sweng.rps.ui.tictactoe.TicTacToeFragment

class MultiplayerTicTacToe(val view: TicTacToeFragment, player: MOVES) : TicTacToeGame(player) {
    override fun putChoice(square: Int) {
        if (!gameRunning)
            return
        val cell = square % 3
        val row = (square / 3)
        if (matrix[row][cell] != MOVES.EMPTY)
            return
        matrix[row][cell] = player
        view.updateUI(square, player)
        calculate()
        player = if (player == MOVES.CROSS) MOVES.CIRCLE else MOVES.CROSS
    }

    override fun gameOver(winner: MOVES) {
        gameRunning = false
        view.gameOver(winner)
    }
}