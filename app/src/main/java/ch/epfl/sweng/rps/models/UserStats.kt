package ch.epfl.sweng.rps.models

data class UserStats(
    val userId: String = "",
    val wins: Int = 0,
    val total_games: Int = 0,
)