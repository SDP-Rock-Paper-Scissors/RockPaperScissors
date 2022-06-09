package ch.epfl.sweng.rps.models.xbstract

import ch.epfl.sweng.rps.models.remote.Hand.Outcome

/**
 * Represerts a point system for a game.
 */
interface PointSystem {
    /**
     * Points awarded for a tie.
     */
    val tie: Int

    /**
     * Points awarded for a loss.
     */
    val loss: Int

    /**
     * Points awarded for a win.
     */
    val win: Int


    /**
     * Returns the points awarded for a given result.
     */
    fun getPoints(result: Outcome): Int {
        return when (result) {
            Outcome.TIE -> tie
            Outcome.LOSS -> loss
            Outcome.WIN -> win
        }
    }

    /**
     * The default default point system.
     */
    class DefaultPointSystem : PointSystem {
        override val tie: Int = 0
        override val loss: Int = -1
        override val win: Int = 1
    }
}


