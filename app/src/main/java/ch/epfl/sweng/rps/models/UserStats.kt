package ch.epfl.sweng.rps.models

data class UserStats(
    val userId: String = "",
    val wins: Int = 0,
    val total_games: Int = 0,
) {
    val winRate: Double
        get() = if (total_games == 0) 0.0 else (wins.toDouble() / total_games)
}