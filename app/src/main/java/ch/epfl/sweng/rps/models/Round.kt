package ch.epfl.sweng.rps.models

import com.google.firebase.Timestamp
import com.google.type.DateTime

data class Round(
    val hands: Map<String, Hand>,
    val timestamp: Timestamp,
    val uid: String,
)