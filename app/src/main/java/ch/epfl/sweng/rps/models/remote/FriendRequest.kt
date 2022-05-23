package ch.epfl.sweng.rps.models.remote

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName
import java.util.*

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
    companion object {
        fun build(from: String, to: String, timestamp: Timestamp = Timestamp.now()): FriendRequest {
            return FriendRequest(listOf(from, to), timestamp, id = "$from-$to")
        }
    }

    val from: String
        get() = users[0]

    val to: String
        get() = users[1]

    enum class Status {
        PENDING, ACCEPTED, REJECTED
    }

    object FIELDS {
        const val USERS = "users"
        const val TIMESTAMP = "timestamp"
        const val STATUS = "status"
        const val ID = "id"
    }
}
