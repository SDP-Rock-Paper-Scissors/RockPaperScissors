package ch.epfl.sweng.rps.services

import ch.epfl.sweng.rps.models.Game
import ch.epfl.sweng.rps.models.Hand
import ch.epfl.sweng.rps.models.Round

interface GameService {
    val gameId: String

    /**
     * Starts the service.
     * This should be called only once.
     *
     * In [FirebaseGameService] this listens to the game document.
     */
    fun startListening(): GameService

    fun isServiceReady(): Boolean

    val isGameFull: Boolean

    suspend fun addRound(): Round

    val currentGame: Game
    suspend fun refreshGame(): Game

    val currentRound: Round

    suspend fun playHand(hand: Hand)

    fun dispose()

    val isGameOver: Boolean
}