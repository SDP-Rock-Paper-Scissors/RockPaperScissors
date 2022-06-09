package ch.epfl.sweng.rps.models.remote

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName
import java.util.*

/**
 * A friend request is a request to be a friend of a user.
 *
 * This document is stored in firebase.
 */
data class FriendRequest(
    @PropertyName(FIELDS.USERS)
    val users: List<String> = listOf(),

    @PropertyName(FIELDS.TIMESTAMP)
    val timestamp: Timestamp = Timestamp(Date(0)),

    @PropertyName(FIELDS.STATUS)
    val status: Status = Status.PENDING,

    @PropertyName(FIELDS.ID)
    val id: String = ""
) {

    /**
     * The requester of the friend request is the first user in the list.
     */
    val from: String
        get() = users[0]

    /**
     * The receiver of the friend request is the second user in the list.
     */
    val to: String
        get() = users[1]

    /**
     * The status of the friend request.
     */
    enum class Status {
        PENDING, ACCEPTED, REJECTED
    }

    companion object {
        fun build(from: String, to: String, timestamp: Timestamp = Timestamp.now()): FriendRequest {
            return FriendRequest(listOf(from, to), timestamp, id = "$from-$to")
        }
    }

    internal object FIELDS {
        const val USERS = "users"
        const val TIMESTAMP = "timestamp"
        const val STATUS = "status"
        const val ID = "id"
    }
}
