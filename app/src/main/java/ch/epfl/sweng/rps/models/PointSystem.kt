package ch.epfl.sweng.rps.models.xbstract

import ch.epfl.sweng.rps.models.remote.Hand.Outcome

interface PointSystem {
    val tie: Int
    val loss: Int
    val win: Int

    fun getPoints(result: Outcome): Int {
        return when (result) {
            Outcome.TIE -> tie
            Outcome.LOSS -> loss
            Outcome.WIN -> win
        }
    }

    class DefaultPointSystem : PointSystem {
        override val tie: Int = 0
        override val loss: Int = -1
        override val win: Int = 1
    }
}


