package ch.epfl.sweng.rps.models

import ch.epfl.sweng.rps.models.Hand.Result

interface PointSystem {
    val draw: Int
    val lose: Int
    val win: Int

    companion object {
        val DEFAULT = DefaultPointSystem()
    }

    fun getPoints(result: Result): Int {
        return when (result) {
            Result.DRAW -> draw
            Result.LOSE -> lose
            Result.WIN -> win
        }
    }
}

class DefaultPointSystem : PointSystem {
    override val draw: Int = 0
    override val lose: Int = -1
    override val win: Int = 1
}
