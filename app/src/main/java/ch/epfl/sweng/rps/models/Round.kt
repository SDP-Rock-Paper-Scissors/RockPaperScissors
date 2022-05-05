package ch.epfl.sweng.rps.models

import ch.epfl.sweng.rps.models.Hand.Result
import ch.epfl.sweng.rps.models.PointSystem.DefaultPointSystem
import com.google.firebase.Timestamp
import java.util.*


data class Round(
    val hands: Map<String, Hand> = mapOf(),
    val timestamp: Timestamp = Timestamp(Date(0)),
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
        }.sortedByDescending { score -> score.results.count { it == Result.WIN } }
            .sortedByDescending { it.points }
    }

    fun getWinner(): String? {
        val scores = computeScores()
        if (scores.isEmpty()) {
            return null
        }
        val first = scores.first()
        val last = scores.last()
        return if (first.points == last.points) {
            null
        } else {
            first.uid
        }
    }

    class Score(
        val uid: String,
        val results: List<Result>,
        val points: Int
    )

}