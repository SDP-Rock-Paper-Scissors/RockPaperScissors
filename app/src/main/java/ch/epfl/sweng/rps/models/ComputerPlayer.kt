package ch.epfl.sweng.rps.models

abstract class ComputerPlayer (
    val computerPlayerId: String,
    val numberOfPossibleMoves: Int){
    /**
     * @return number representing a move from possible range [0, ..., numberOfMoves - 1]
     */
    abstract fun makeMove(): Hand
}