package ch.epfl.sweng.rps.models

interface ComputerPlayer{
    /**
     * @param numberOfMoves - total number of moves possible in a game
     * @return number representing a move from possible range [0, ..., numberOfMoves - 1]
     */
    fun makeMove(numberOfMoves: Int): Hand
}