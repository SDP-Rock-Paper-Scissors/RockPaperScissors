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


    val mode: GameMode get() = GameMode.fromString(game_mode)
    val gameMode: GameMode get() = GameMode.fromString(game_mode)
}
