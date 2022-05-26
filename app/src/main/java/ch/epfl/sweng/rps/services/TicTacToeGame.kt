package ch.epfl.sweng.rps.services

abstract class TicTacToeGame(var player: MOVES) {
    val matrix = Array(3) { Array(3) { MOVES.EMPTY } }
    var gameRunning = true

    /**
     * Puts the move in the specified square
     * @param square the square from a range 0-8
     */
    abstract fun putChoice(square: Int)

    /**
     * This function is called automatically on game over.
     * @return returns the winner move or MOVES.EMPTY in case of draw
     */
    abstract fun gameOver(winner: MOVES)

    /**
     * Checks if there is a win or a draw for the current TTT game.
     * @return true on gameover , false if the game can still be continued.
     */
    fun calculate(): Boolean {
        /*
        This block checks for a win in the tictactoe matrix
         */
        for (i in (0..2)) {
            if (checkCell(1, 0, i)) return true
            if (checkCell(0, i, 0)) return true
        }
        if (checkCell(2, 0, 0)) return true
        if (checkCell(3, 2, 0)) return true
        if (!matrix.flatten().filter { it == MOVES.EMPTY }.any()) { //Checks if there is at least 1 free cell
            gameOver(MOVES.EMPTY)
            return true
        }
        return false
    }

    /**
     * Blackmagic to check if the specified cell is in a line of len 3 of the same figure.
     * Should only be called internally by calculate().
     */
    private fun checkCell(d: Int, cx: Int, cy: Int): Boolean {
        val dirx = arrayOf(0, 1, 1, -1) // 0 = ↓ 1 = ↘ 2 = ↙
        val diry = arrayOf(1, 0, 1, 1)
        var cnt = 0
        val choice = matrix[cy][cx]
        if (choice == MOVES.EMPTY)
            return false
        for (i in 0..2) {
            if (cx + i * dirx[d] > 2 || cx + i * dirx[d] < 0 || cy + i * diry[d] > 2 || cy + i * diry[d] < 0)
                return false
            if (choice == matrix[cy + i * diry[d]][cx + i * dirx[d]])
                cnt++
        }
        if (cnt == 3) {
            gameOver(choice)
            return true
        }
        return false
    }

    enum class MOVES {
        CROSS, CIRCLE, EMPTY
    }

    enum class MODE {
        PC, MULTIPLAYER
    }

}