package ch.epfl.sweng.rps.services

import ch.epfl.sweng.rps.models.Game
import ch.epfl.sweng.rps.models.Hand
import ch.epfl.sweng.rps.models.Round
import com.google.firebase.firestore.FirebaseFirestoreException

interface GameService {
    val gameId: String

    /**
     * Starts the service.
     * This should be called only once.
     *
     * In [FirebaseGameService] this listens to the game document.
     */
    fun startListening(): GameService


    val isGameFull: Boolean

    suspend fun addRound(): Round

    val currentGame: Game
    suspend fun refreshGame(): Game

    val currentRound: Round

    suspend fun playHand(hand: Hand)

    fun dispose()

    val isGameOver: Boolean

    val isDisposed: Boolean
    val active: Boolean
    fun stopListening()
    val error: FirebaseFirestoreException?

    class GameServiceException : Exception {
        constructor(message: String) : super(message)
        constructor(message: String, cause: Throwable) : super(message, cause)
        constructor(cause: Throwable) : super(cause)
    }
}