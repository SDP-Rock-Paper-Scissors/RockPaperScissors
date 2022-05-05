package ch.epfl.sweng.rps.models

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
    val edition: GameEdition
) {
    /**
     * The type of opponents.
     *
     * [LOCAL] - A local game with multiple players on the same device.
     *
     * [PVP] - A player vs player game (online).
     *
     * [PC] - A local game with a computer opponent.
     */
    enum class Type {
        LOCAL, PVP, PC
    }

    /**
     * Represents a game edition.
     *
     * [id] must be unique, short, lowercase, and contain only letters, numbers, and underscores.
     * @property name The name of the game edition.
     * @property id The id of the game edition.
     */
    enum class GameEdition(val id: String) {
        RockPaperScissors("rps"),
        TicTacToe("ttt");

        companion object {
            fun fromId(id: String): GameEdition {
                return ids[id] ?: throw IllegalArgumentException("Unknown game edition id: $id")
            }

            private val ids by lazy { values().associateBy { it.id } }
        }
    }

    companion object {
        // "P:5,G:PC,R:3,T:0", //5 players, against computer, 3 rounds, 0 time limit (no time limit)
        fun fromString(s: String): GameMode {
            val map = s.split(",")
                .map { it.split(":", limit = 2) }
                .associate { it[0] to it[1] }
            val maxPlayerCount = map["P"]!!.toInt()
            val gameType = map["MT"]!!
            val rounds = map["R"]!!.toInt()
            val timeLimit = map["T"]!!.toInt()
            val edition = map["GE"]!!
            return GameMode(
                playerCount = maxPlayerCount,
                type = Type.valueOf(gameType),
                timeLimit = timeLimit,
                rounds = rounds,
                edition = GameEdition.fromId(edition)
            )
        }
    }

    override fun toString(): String = toGameModeString()

    fun toGameModeString(): String {
        val map = listOf(
            "P" to playerCount.toString(),
            "MT" to type.name,
            "R" to rounds.toString(),
            "T" to timeLimit.toString(),
            "GE" to edition.id
        )
        return map
            .sortedBy { it.first }
            .joinToString(",") { "${it.first}:${it.second}" }
    }


    fun String.toGameMode(): GameMode {
        return fromString(this)
    }
}