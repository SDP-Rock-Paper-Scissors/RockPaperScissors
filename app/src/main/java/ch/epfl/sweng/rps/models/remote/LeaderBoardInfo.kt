package ch.epfl.sweng.rps.models.remote

import android.net.Uri

data class LeaderBoardInfo(
    var username: String = "",
    var uid: String = "",
    var userProfilePictureUrl: Uri? = null,
    var point: Int = 0
)
