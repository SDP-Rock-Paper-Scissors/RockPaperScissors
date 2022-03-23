package ch.epfl.sweng.rps.models

import com.google.firebase.Timestamp

data class FriendRequest(
    val from: String,
    val timestamp: Timestamp = Timestamp.now(),
    val accepted: Boolean = false,
)
