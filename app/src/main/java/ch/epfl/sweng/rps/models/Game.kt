package ch.epfl.sweng.rps.models

import com.google.firebase.Timestamp
import java.util.*


/**
 * Represents a game.
 * @property id Unique ID of the game
 * @property players The players of the game
 * @property mode The game mode
 * @property rounds an array of rounds (Note that this is a map, not an actual array)
 * @property current_round This is the current round
 * @property timestamp The timestamp of the game
 */
data class Game(
    val id: String = "",
    val players: List<String> = listOf(),
    val rounds: Map<String, Round> = mapOf(),
    val current_round: Int = 0,
    val game_mode: String = "",
    val done: Boolean = true,
    val timestamp: Timestamp = Timestamp(Date(0)),
    val player_count: Int = 0
) {
    /**
     * Represents a game mode.
     * @property playerCount The number of players in the game, including computers.
     * @property type The type of opponents
     * @property timeLimit The time limit, if any, in seconds.
     */
    data class GameMode(
        val playerCount: Int,
        val type: Type,
        val rounds: Int,
        val timeLimit: Int,
    ) {
        enum class Type {
            LOCAL, PVP, PC
        }

        companion object {
            // "P:5,G:PC,R:3,T:0", //5 players, against computer, 3 rounds, 0 time limit (no time limit)
            fun fromString(s: String): GameMode {
                val map = s.split(",")
                    .map { it.trim().split(":", limit = 2) }
                    .associate { it[0] to it[1] }
                val maxPlayerCount = map["P"]!!.toInt()
                val gameType = map["G"]!!
                val rounds = map["R"]!!.toInt()
                val timeLimit = map["T"]!!.toInt()
                return GameMode(
                    playerCount = maxPlayerCount,
                    type = Type.valueOf(gameType),
                    timeLimit = timeLimit,
                    rounds = rounds
                )
            }
        }

        fun toGameModeString(): String {
            val map = listOf(
                "P" to playerCount.toString(),
                "G" to type.name,
                "R" to rounds.toString(),
                "T" to timeLimit.toString()
            )
            return map.sortedBy { it.first }
                .joinToString(",") { it.first + ":" + it.second }
        }
    }

    val mode: GameMode get() = GameMode.fromString(game_mode)
}
