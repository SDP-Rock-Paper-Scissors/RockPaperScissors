package ch.epfl.sweng.rps.models

import com.google.firebase.Timestamp

data class Round(
    val hands: Map<String, Hand>,
    val timestamp: Timestamp,
    val uid: String,
)