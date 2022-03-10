package ch.epfl.sweng.rps.models

import ch.epfl.sweng.rps.serialization.TimestampAsListSerializer
import com.google.firebase.Timestamp
import kotlinx.serialization.Serializable

@Serializable
data class Round(
    val hands: Map<String, Hand>,
    @Serializable(with = TimestampAsListSerializer::class)
    val timestamp: Timestamp,
    val uid: String,
)