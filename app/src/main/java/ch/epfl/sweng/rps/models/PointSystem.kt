package ch.epfl.sweng.rps.models

interface PointSystem {
    val draw: Int
    val lose: Int
    val win: Int

    companion object {
        val DEFAULT = object : PointSystem {
            override val lose: Int = -1
            override val draw: Int = 0
            override val win: Int = 1
        }
    }
}
