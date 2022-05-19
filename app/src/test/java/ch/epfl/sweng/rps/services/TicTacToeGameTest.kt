package ch.epfl.sweng.rps.services

import org.junit.Before
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TicTacToeGameTest {
    lateinit var ticTacToe:TicTacToeGame
    @BeforeEach
    fun setup(){
        ticTacToe = object : TicTacToeGame(MOVES.CIRCLE){
            override fun putChoice( square: Int) {
                val cell = square % 3
                val row = (square / 3)
                matrix[row][cell] = player
            }

            override fun gameOver(winner: MOVES) {
                gameRunning = false
            }
        }
    }
    @Test
    fun TTTcorrectlycalculatesWinRow(){
        assert(ticTacToe.gameRunning)
        ticTacToe.putChoice( 0)
        ticTacToe.putChoice( 1)
        ticTacToe.putChoice( 2)
        ticTacToe.calculate()
        assert(!ticTacToe.gameRunning)
    }
    @Test
    fun TTTcorrectlycalculatesWinCol(){
        assert(ticTacToe.gameRunning)
        ticTacToe.putChoice(0)
        ticTacToe.putChoice(3)
        ticTacToe.putChoice(6)
        ticTacToe.calculate()
        assert(!ticTacToe.gameRunning)
    }
    @Test
    fun TTTcorrectlycalculatesWinDiag1(){
        assert(ticTacToe.gameRunning)
        ticTacToe.putChoice(2)
        ticTacToe.putChoice(4)
        ticTacToe.putChoice(6)
        ticTacToe.calculate()
        assert(!ticTacToe.gameRunning)
    }
    @Test
    fun TTTcorrectlycalculatesWinDiag2(){
        assert(ticTacToe.gameRunning)
        ticTacToe.putChoice(0)
        ticTacToe.calculate()
        assert(ticTacToe.gameRunning)
        ticTacToe.putChoice(4)
        ticTacToe.putChoice(8)
        ticTacToe.calculate()
        assert(!ticTacToe.gameRunning)
    }
}