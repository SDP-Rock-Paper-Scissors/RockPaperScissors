package ch.epfl.sweng.rps.models

import ch.epfl.sweng.rps.models.Hand.Result
import ch.epfl.sweng.rps.models.PointSystem.DefaultPointSystem
import com.google.firebase.Timestamp
import java.util.*


sealed class Round() {

    open val hands: Map<String, Hand>
        get() = throw IllegalStateException(
            "You can't access hands of a ${this::class.simpleName}.\n" +
                    "You need to make sure the round is a ${Rps::class.simpleName}."
        )
    abstract val timestamp: Timestamp
    abstract val edition: GameMode.GameEdition

    data class Rps(
        override val hands: Map<String, Hand> = mapOf(),
        override val timestamp: Timestamp = Timestamp(Date(0))
    ) : Round() {
        override val edition: GameMode.GameEdition = GameMode.GameEdition.RockPaperScissors
        override fun computeScores(pointSystem: PointSystem): List<Score> {

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

        override fun getWinner(): String? {
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
    }

    data class TicTacToe(
        val board: List<Int?> = (1..9).map { null },
        val players: Map<String, Int> = mapOf(),
        val turn: String = "",
        override val timestamp: Timestamp = Timestamp(Date(0))
    ) : Round() {
        override val edition: GameMode.GameEdition = GameMode.GameEdition.TicTacToe

        override fun computeScores(pointSystem: PointSystem): List<Score> {
            val winner = computeWinner()
            when {
                winner != null -> return players.map {
                    val result = if (it.value == winner) Result.WIN else Result.LOSS
                    Score(
                        it.key,
                        listOf(result),
                        pointSystem.getPoints(result)
                    )
                }
                isGameOver -> return players.map {
                    Score(
                        it.key,
                        results = listOf(Result.TIE),
                        points = pointSystem.getPoints(Result.TIE)
                    )
                }
                else -> return emptyList()
            }
        }

        override fun getWinner(): String? {
            val winner = computeWinner()
            return if (winner != null) {
                players.entries.firstOrNull { it.value == winner }?.key
            } else {
                null
            }
        }

        val isGameOver: Boolean get() = board.none { it == null }

        // Look for a diagonal, a row or a column full of the same value
        // Return the value if found, null otherwise
        private fun computeWinner(): Int? {
            val diag = listOf(0, 4, 8).map { board[it] }
            val diag2 = listOf(2, 4, 6).map { board[it] }
            val rows = board.chunked(3)
            val initialValues = Array<Int?>(3) { null }
            val cols = board.foldRightIndexed(
                (0 until 3).map { initialValues.toMutableList() }
            ) { index, elem, acc ->
                acc[index % 3][index / 3] = elem
                acc
            }
            val all = rows + cols + listOf(diag, diag2)
            println("Rows: $rows")
            println("Cols: $cols")
            println("Diag: $diag")
            println("Diag2: $diag2")
            return all.firstNotNullOfOrNull { list ->
                val first = list.first()
                if (list.all { it == first }) {
                    first
                } else {
                    null
                }
            }
        }
    }

    abstract fun computeScores(pointSystem: PointSystem = DefaultPointSystem()): List<Score>

    abstract fun getWinner(): String?

    class Score(
        val uid: String,
        val results: List<Result>,
        val points: Int
    )


    object FIELDS {
        const val UID = "uid"
        const val TIMESTAMP = "timestamp"
        const val EDITION = "edition"
        const val PLAYERS = "players"
        const val TURN = "turn"
        const val BOARD = "board"
    }
}