package ch.epfl.sweng.rps.services

import ch.epfl.sweng.rps.ui.tictactoe.TicTacToeFragment
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TicTacToeMultiplayerTest {
    lateinit var tictactoe:MultiplayerTicTacToe
    lateinit var view: TicTacToeFragment
    var player = TicTacToeGame.MOVES.CIRCLE
    @BeforeEach
    fun setup(){
        view = mockk<TicTacToeFragment>(relaxed = true)
        tictactoe = MultiplayerTicTacToe(view,player)
    }
    @Test
    fun putChoiceCorrectlyPutsChoice() {
        tictactoe.putChoice(0)
        assert(tictactoe.matrix[0][0] == TicTacToeGame.MOVES.CIRCLE)
        tictactoe.putChoice(1)
        assert(tictactoe.matrix[0][1] == TicTacToeGame.MOVES.CROSS)
        tictactoe.putChoice(3)
        assert(tictactoe.matrix[1][0] == TicTacToeGame.MOVES.CIRCLE)
        tictactoe.putChoice(5)
        assert(tictactoe.matrix[1][2] == TicTacToeGame.MOVES.CROSS)
        tictactoe.putChoice(6)
        assert(tictactoe.matrix[2][0] == TicTacToeGame.MOVES.CIRCLE)
        assert(!tictactoe.gameRunning)
        tictactoe.putChoice(2)
        assert(tictactoe.matrix[0][2] == TicTacToeGame.MOVES.EMPTY)
    }
    @Test
    fun putChoice2TimesDoesNotChange(){
        tictactoe.putChoice(0)
        assert(tictactoe.matrix[0][0] == TicTacToeGame.MOVES.CIRCLE)
        tictactoe.putChoice(0)
        assert(tictactoe.matrix[0][0] == TicTacToeGame.MOVES.CIRCLE)
    }
}