package ch.epfl.sweng.rps.models.remote

import ch.epfl.sweng.rps.models.remote.GameMode.GameEdition
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import java.util.*


/**
 * Represents a game.
 * @property id Unique ID of the game
 * @property players The players of the game
 * @property gameMode The game mode
 * @property rounds an array of rounds (Note that this is a map, not an actual array)
 * @property current_round The current round
 * @property timestamp The timestamp of the game
 */
sealed class Game {
    abstract val id: String
    abstract val players: List<String>
    abstract val rounds: Map<String, Round>
    abstract val current_round: Int
    abstract val game_mode: String
    abstract val done: Boolean
    abstract val timestamp: Timestamp
    abstract val player_count: Int
    abstract val edition: GameEdition
    abstract val started: Boolean

    val gameMode: GameMode get() = GameMode.fromString(game_mode)

    data class Rps(
        override val id: String = "",
        override val players: List<String> = listOf(),
        override val rounds: Map<String, Round.Rps> = mapOf(),
        override val current_round: Int = 0,
        override val game_mode: String = "",
        override val done: Boolean = false,
        override val timestamp: Timestamp = Timestamp(Date(0)),
        override val player_count: Int = 0,
        override val started: Boolean = false,
    ) : Game() {
        override val edition: GameEdition = GameEdition.RockPaperScissors
    }


    data class TicTacToe(
        override val id: String = "",
        override val players: List<String> = listOf(),
        override val rounds: Map<String, Round.TicTacToe> = mapOf(),
        override val current_round: Int = 0,
        override val game_mode: String = "",
        override val done: Boolean = false,
        override val timestamp: Timestamp = Timestamp(Date(0)),
        override val player_count: Int = 0,
        override val started: Boolean = false,
    ) : Game() {
        override val edition: GameEdition = GameEdition.TicTacToe
    }

    internal object FIELDS {
        const val STARTED = "started"
        const val PLAYERS = "players"
        const val ROUNDS = "rounds"
        const val CURRENT_ROUND = "current_round"
        const val GAME_MODE = "game_mode"
        const val DONE = "done"
        const val TIMESTAMP = "timestamp"
        const val PLAYER_COUNT = "player_count"
    }

    companion object {

        /**
         * Creates a game from a document snapshot
         */
        fun fromDocumentSnapshot(document: DocumentSnapshot): Game? {
            val editionString = document["edition"] as String?
            val gameMode = document["game_mode"] as String?
            val edition =
                editionString?.let { GameEdition.valueOf(it) }
                    ?: gameMode?.let { GameMode.fromString(it).edition }
                    ?: throw IllegalArgumentException("Document has no edition or game mode")

            val type = when (edition) {
                GameEdition.RockPaperScissors -> Rps::class.java
                GameEdition.TicTacToe -> TicTacToe::class.java
            }
            return document.toObject(type)
        }
    }
}
