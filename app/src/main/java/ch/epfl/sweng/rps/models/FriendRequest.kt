package ch.epfl.sweng.rps.models

import ch.epfl.sweng.rps.serialization.TimestampAsListSerializer
import com.google.firebase.Timestamp
import kotlinx.serialization.Serializable

@Serializable
data class FriendRequest(
    val from: String,
    @Serializable(with = TimestampAsListSerializer::class)
    val timestamp: Timestamp
) {
    constructor(
        from: String,
    ) : this(
        from, timestamp = Timestamp.now()
    )
}

