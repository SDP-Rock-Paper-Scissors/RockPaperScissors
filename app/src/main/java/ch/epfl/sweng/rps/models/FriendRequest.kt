package ch.epfl.sweng.rps.models

import com.google.firebase.Timestamp

data class FriendRequest(
    val from: String,
    val timestamp: Timestamp
) {
    constructor(
        from: String,
    ) : this(
        from, timestamp = Timestamp.now()
    )
}
