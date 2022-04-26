package ch.epfl.sweng.rps.models

import android.net.Uri

data class LeaderBoardInfo(
    val username: String,
    val uid: String,
    val userProfilePictureUrl: Uri,
    val point: Int
    )
