package ch.epfl.sweng.rps.models.remote

import com.google.firebase.Timestamp
import java.util.*

data class Invitation(
    val game_id: String? = null,
    val timestamp: Timestamp = Timestamp(Date(0)),
    val uids: List<String> = listOf(),
    val id: String = "",
    val status: FriendRequest.Status = FriendRequest.Status.PENDING,
) {
    val from: String
        get() = uids[0]

    val to: String
        get() = uids[1]

    object FIELDS {
        const val GAME_ID = "game_id"
        const val TIMESTAMP = "timestamp"
        const val UIDS = "uids"
        const val ID = "id"
        const val STATUS = "status"
    }
}

typealias GameInvitation = Invitation