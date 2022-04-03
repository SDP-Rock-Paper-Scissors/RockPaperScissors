package ch.epfl.sweng.rps.models

import ch.epfl.sweng.rps.models.Hand.Result
import com.google.firebase.Timestamp

/*
 * This file is part of rps-android
 * pa
 */
data class Round(
    val hands: Map<String, Hand>,
    val timestamp: Timestamp,
) {
    fun computeScores(pointSystem: PointSystem = DefaultPointSystem()): List<Score> {
        val points = hashMapOf<String, List<Result>>()
        for ((uid, hand) in hands) {
            for ((uid2, hand2) in hands) {
                if (uid != uid2) {
                    points[uid] = listOf(
                        *(points[uid] ?: emptyList()).toTypedArray(),
                        (hand vs hand2)
                    )
                }
            }
        }
        return points.map { res ->
            Score(
                res.key,
                results = res.value,
                points = res.value.sumOf { pointSystem.getPoints(it) })
        }.sortedByDescending { it.points }
    }

    class Score(
        val uid: String,
        val results: List<Result>,
        val points: Int
    )
}