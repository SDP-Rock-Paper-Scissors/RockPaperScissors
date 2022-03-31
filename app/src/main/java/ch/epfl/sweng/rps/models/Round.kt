package ch.epfl.sweng.rps.models

import ch.epfl.sweng.rps.models.Hand.Result
import com.google.firebase.Timestamp

data class Round(
    val hands: Map<String, Hand>,
    val timestamp: Timestamp,
    val id: String,
    val game_id: String
) {
    fun computeScores(pointSystem: PointSystem = PointSystem.DEFAULT): List<Score> {
        val points = hashMapOf<String, Int>()
        for ((uid, hand) in hands) {
            for ((uid2, hand2) in hands) {
                if (uid != uid2) {
                    points[uid] = (points[uid] ?: 0) + when (hand vs hand2) {
                        Result.WIN -> pointSystem.win
                        Result.LOSE -> pointSystem.lose
                        Result.DRAW -> pointSystem.draw
                    }
                }
            }
        }
        return points.map { Score(it.key, it.value) }.sortedByDescending { it.score }
    }

    data class Score(
        val uid: String,
        val score: Int
    )
}