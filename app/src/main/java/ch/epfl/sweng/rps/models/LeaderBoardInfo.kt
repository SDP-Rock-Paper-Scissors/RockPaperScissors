package ch.epfl.sweng.rps.models

import android.net.Uri
import android.net.Uri.EMPTY

data class LeaderBoardInfo(
    var username: String = "",
    var uid: String = "",
    var userProfilePictureUrl: Uri = EMPTY,
    var point: Int = 0
    )
