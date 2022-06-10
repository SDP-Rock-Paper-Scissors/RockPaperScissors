package ch.epfl.sweng.rps.models.remote

import android.net.Uri

/**
 * A group of *LeaderBoardInfo*.
 *
 * This class is used to organize all information for leaderboard.
 *
 * @param username is user's name to present on leaderboard page.
 * @param uid is user's id.
 * @param userProfilePictureUrl is user's avatar to present on leaderboard page.
 * @param point is the score of selected mode to present on leaderboard page.
 * @constructor Creates an empty group of LeaderBoardInfo data class.
 */
data class LeaderBoardInfo(
    var username: String = "",
    var uid: String = "",
    var userProfilePictureUrl: Uri? = null,
    var point: Int = 0
    )
