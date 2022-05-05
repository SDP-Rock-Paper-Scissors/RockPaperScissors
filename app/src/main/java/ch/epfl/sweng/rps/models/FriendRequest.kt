package ch.epfl.sweng.rps.models

import com.google.firebase.Timestamp

data class FriendRequest(
    val users: List<String> = listOf(),
    val timestamp: Timestamp = Timestamp.now(),
    val status: Status = Status.PENDING,
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
}
