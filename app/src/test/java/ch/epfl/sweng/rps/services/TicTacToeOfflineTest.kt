package ch.epfl.sweng.rps.services

import ch.epfl.sweng.rps.ui.tictactoe.TicTacToeFragment
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TicTacToeOfflineTest {
    lateinit var tictactoe:OfflineTicTacToe
    lateinit var view: TicTacToeFragment
    var choice = Pair(0,1)
    @BeforeEach
    fun setup(){
        view = mockk<TicTacToeFragment>(relaxed = true)
        tictactoe = OfflineTicTacToe(view)
        val opponent = object : TicTacToeOpponent {
            override fun makeMove(): Pair<Int, Int> {
               return choice
            }
        }
        tictactoe.player2 = opponent
    }
    @Test
    fun putChoiceCorrectlyPutsChoice() {
        choice = Pair(0,1)
        tictactoe.putChoice(TicTacToeGame.MOVES.CIRCLE, 0)
        assert(tictactoe.matrix[0][0] == TicTacToeGame.MOVES.CIRCLE)
        assert(tictactoe.matrix[0][1] == TicTacToeGame.MOVES.CROSS)
        choice = Pair(1, 2)
        tictactoe.putChoice(TicTacToeGame.MOVES.CIRCLE, 3)
        assert(tictactoe.matrix[1][0] == TicTacToeGame.MOVES.CIRCLE)
        assert(tictactoe.matrix[1][2] == TicTacToeGame.MOVES.CROSS)
        choice = Pair(2,2)
        tictactoe.putChoice(TicTacToeGame.MOVES.CIRCLE, 6)
        assert(tictactoe.matrix[2][0] == TicTacToeGame.MOVES.CIRCLE)
        assert(!tictactoe.gameRunning)
        tictactoe.putChoice(TicTacToeGame.MOVES.CIRCLE,2)
        assert(tictactoe.matrix[0][2] == TicTacToeGame.MOVES.EMPTY)
    }
    @Test
    fun putChoice2TimesDoesNotChange(){
        tictactoe.putChoice(TicTacToeGame.MOVES.CIRCLE, 0)
        assert(tictactoe.matrix[0][0] == TicTacToeGame.MOVES.CIRCLE)
        tictactoe.putChoice(TicTacToeGame.MOVES.CROSS, 0)
        assert(tictactoe.matrix[0][0] == TicTacToeGame.MOVES.CIRCLE)
    }
}