package ch.epfl.sweng.rps.models

import ch.epfl.sweng.rps.models.Hand.Result
import com.google.firebase.Timestamp

data class Round(
    val hands: Map<String, Hand>,
    val timestamp: Timestamp,
    val roundId: String,
) {
    private var userToPointsList:  List<Score> = listOf()
    private var _winnerId: String = ""
    val winnerId: String
        get() = _winnerId

    fun computeScores(pointSystem: PointSystem = PointSystem.DEFAULT): List<Score> {
        val userToPoints: HashMap<String, Int> = HashMap()
        for ((uid, hand) in hands) {
            for ((uid2, hand2) in hands) {
                if (uid != uid2) {
                    userToPoints[uid] = (userToPoints[uid] ?: 0) + when (hand vs hand2) {
                        Result.WIN -> pointSystem.win
                        Result.LOSE -> pointSystem.lose
                        Result.DRAW -> pointSystem.draw
                    }
                }
            }
        }
        userToPointsList = userToPoints.map { Score(it.key, it.value) }.sortedByDescending { it.score }
        determineWinner()
        return userToPointsList
    }

    data class Score(
        val uid: String,
        val score: Int
    )

    /**
     * Simplified version for 2 people.
     */
    private fun determineWinner(){
        if (userToPointsList.first().score == 1)
            _winnerId = userToPointsList.first().uid
        else{
            _winnerId = ""
        }
    }

}