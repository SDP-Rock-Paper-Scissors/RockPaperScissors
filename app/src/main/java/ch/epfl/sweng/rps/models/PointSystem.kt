package ch.epfl.sweng.rps.models

import ch.epfl.sweng.rps.models.Hand.Result

interface PointSystem {
    val tie: Int
    val loss: Int
    val win: Int

    fun getPoints(result: Result): Int {
        return when (result) {
            Result.TIE -> tie
            Result.LOSS -> loss
            Result.WIN -> win
        }
    }

    class DefaultPointSystem : PointSystem {
        override val tie: Int = 0
        override val loss: Int = -1
        override val win: Int = 1
    }
}


