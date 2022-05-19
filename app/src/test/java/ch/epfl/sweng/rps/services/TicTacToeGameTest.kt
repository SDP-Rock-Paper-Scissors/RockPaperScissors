package ch.epfl.sweng.rps.services

import org.junit.Before
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TicTacToeGameTest {
    lateinit var ticTacToe:TicTacToeGame
    @BeforeEach
    fun setup(){
        ticTacToe = object : TicTacToeGame(){
            override fun putChoice(move: MOVES, square: Int) {
                val cell = square % 3
                val row = (square / 3)
                matrix[row][cell] = move
            }

            override fun gameOver(winner: MOVES) {
                gameRunning = false
            }
        }
    }
    @Test
    fun TTTcorrectlycalculatesWinRow(){
        assert(ticTacToe.gameRunning)
        ticTacToe.putChoice(TicTacToeGame.MOVES.CIRCLE, 0)
        ticTacToe.putChoice(TicTacToeGame.MOVES.CIRCLE, 1)
        ticTacToe.putChoice(TicTacToeGame.MOVES.CIRCLE, 2)
        ticTacToe.calculate()
        assert(!ticTacToe.gameRunning)
    }
    @Test
    fun TTTcorrectlycalculatesWinCol(){
        assert(ticTacToe.gameRunning)
        ticTacToe.putChoice(TicTacToeGame.MOVES.CIRCLE, 0)
        ticTacToe.putChoice(TicTacToeGame.MOVES.CIRCLE, 3)
        ticTacToe.putChoice(TicTacToeGame.MOVES.CIRCLE, 6)
        ticTacToe.calculate()
        assert(!ticTacToe.gameRunning)
    }
    @Test
    fun TTTcorrectlycalculatesWinDiag1(){
        assert(ticTacToe.gameRunning)
        ticTacToe.putChoice(TicTacToeGame.MOVES.CIRCLE, 2)
        ticTacToe.putChoice(TicTacToeGame.MOVES.CIRCLE, 4)
        ticTacToe.putChoice(TicTacToeGame.MOVES.CIRCLE, 6)
        ticTacToe.calculate()
        assert(!ticTacToe.gameRunning)
    }
    @Test
    fun TTTcorrectlycalculatesWinDiag2(){
        assert(ticTacToe.gameRunning)
        ticTacToe.putChoice(TicTacToeGame.MOVES.CIRCLE, 0)
        ticTacToe.calculate()
        assert(ticTacToe.gameRunning)
        ticTacToe.putChoice(TicTacToeGame.MOVES.CIRCLE, 4)
        ticTacToe.putChoice(TicTacToeGame.MOVES.CIRCLE, 8)
        ticTacToe.calculate()
        assert(!ticTacToe.gameRunning)
    }
}