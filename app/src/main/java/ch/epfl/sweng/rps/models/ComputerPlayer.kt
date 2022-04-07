package ch.epfl.sweng.rps.models

abstract class ComputerPlayer (
    val computerPlayerId: String)
{
    /**
     * @return number representing a move from possible range [0, ..., numberOfMoves - 1]
     */
    abstract fun makeMove(): Hand
}