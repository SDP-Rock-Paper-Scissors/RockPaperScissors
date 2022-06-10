package ch.epfl.sweng.rps.models.ui

/**
 * A group of *UserStat*.
 *
 * This class is used to organize all needed information for Stats data from Firebase.
 *
 * @param gameId is the id for each game to enmurate.
 * @param date is the start date for the specific game.
 * @param opponents is opponent list for the specifc game.
 * @param gameMode is the game mode of the specific game.
 * @param userScore is the calculated user's score who current logged in.
 * @param opponentScore is calculated opponents' score.
 * @param outCome with the camparison of userScore and opponentScore to find the outcome for user.
 * @constructor Creates an empty group of UserStat data class.
 */
data class UserStat(

    var gameId: String = "",
    var date: String = "",
    var opponents: String = "",
    var gameMode: String = "",
    var userScore: String = "",
    var opponentScore: String = "",
    var outCome: Int = 0,

)