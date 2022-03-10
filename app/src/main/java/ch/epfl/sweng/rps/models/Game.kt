package ch.epfl.sweng.rps.models


/**
 * Represents a game.
 * @property uid Unique ID of the game
 * @property players The
 * @property mode The time limit, if any, in seconds.
 */
data class Game(
    val uid: String,
    val players: List<Uid>,
    val mode: Mode
) {
    init {
        assert(players.size == mode.playerCount) { "The number of players doesn't coincide with the gameMode !" }
    }

    /**
     * Represents a game mode.
     * @property playerCount The number of players in the game, including computers.
     * @property type The type of opponents
     * @property time The time limit, if any, in seconds.
     */
    data class Mode(val playerCount: Int, val type: Type, val time: Int?, val rounds: Int) {
        enum class Type {
            LOCAL, ONLINE, PC
        }
    }

    data class Uid(
        val uid: String,
        val isComputer: Boolean = false
    )
}
