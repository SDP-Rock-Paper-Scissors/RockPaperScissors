package ch.epfl.sweng.rps.models.remote

data class TotalScore(
    val uid: String? = "",
    val RPSScore: Int? = 0,
    val TTTScore: Int? = 0
)