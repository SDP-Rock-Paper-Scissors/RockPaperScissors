package ch.epfl.sweng.rps.models

abstract class ComputerPlayer (
    private val _computerPlayerId: String,
    private val _numberOfPossibleMoves: Int){
    val computerPlayerId: String
        get() = _computerPlayerId
    val numberOfPossibleMoves: Int
        get() = _numberOfPossibleMoves
    /**
     * @return number representing a move from possible range [0, ..., numberOfMoves - 1]
     */
    abstract fun makeMove(): Hand
}