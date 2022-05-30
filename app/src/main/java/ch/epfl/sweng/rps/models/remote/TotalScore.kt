package ch.epfl.sweng.rps.models.remote

/**
 * A group of *ScoreInfo*.
 *
 * This class is used as an adapter to fetch data from Firebase.
 *
 * @param uid is user's id.
 * @param RPSScore is user's score of Rock Paper Scissor.
 * @param TTTScore is user's score of Tic Tac Toe.
 * @constructor Creates an empty group of ScoreInfo data class.
 */

data class TotalScore(
    val uid: String? = "",
    val RPSScore: Int? = 0,
    val TTTScore: Int? = 0
)