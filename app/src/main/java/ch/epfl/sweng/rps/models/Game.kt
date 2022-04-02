package ch.epfl.sweng.rps.models

import com.google.firebase.Timestamp


/**
 * Represents a game.
 * @property uid Unique ID of the game
 * @property players The
 * @property mode The game mode
 */
data class Game(
    val id: String,
    val players: List<String>,
    val rounds: List<String>,
    val game_mode: String,
    val done: Boolean,
    val timestamp: Timestamp,
    val player_count: Int
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
            return "P:$playerCount,G:$type,R:$rounds,T:$timeLimit"
        }
    }

    data class Uid(
        val uid: String,
        val isComputer: Boolean = false
    )

    val mode: GameMode
        get() = GameMode.fromString(game_mode)
}
