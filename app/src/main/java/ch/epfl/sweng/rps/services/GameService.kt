package ch.epfl.sweng.rps.services

import ch.epfl.sweng.rps.db.FirebaseReferences
import ch.epfl.sweng.rps.db.FirebaseRepository
import ch.epfl.sweng.rps.models.Game
import ch.epfl.sweng.rps.models.Hand
import ch.epfl.sweng.rps.models.Round
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue.arrayUnion
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await
import java.util.*

class GameService(
    private val firebase: FirebaseReferences,
    private val firebaseRepository: FirebaseRepository,
    val gameId: String,
) {
    private var game: Game? = null
    private var disposed = false
    private var gameRef = firebase.gamesCollection.document(gameId)
    private lateinit var listenerRegistration: ListenerRegistration

    fun start(): GameService {
        checkNotDisposed()
        if (::listenerRegistration.isInitialized) {
            throw IllegalStateException("Listener already registered")
        }
        listenerRegistration =
            gameRef.addSnapshotListener { value, error ->
                if (error != null) {
                    throw error
                } else {
                    game = value?.toObject<Game>()
                }
            }
        return this
    }

    fun isServiceReady(): Boolean {
        return game != null
    }

    fun currentGame(): Game {
        checkNotDisposed()
        if (!isServiceReady()) {
            throw IllegalStateException("Game service has not received a game yet")
        }
        return game!!
    }

    fun isGameFull(): Boolean {
        return currentGame().players.size == currentGame().mode.playerCount
    }

    suspend fun addRound() {
        checkNotDisposed()
        val game = refreshGame()
        if (game.players.first() != firebaseRepository.getCurrentUid()) {
            throw IllegalStateException("Only the first player can add a round")
        }
        val round = Round(
            hands = emptyMap(),
            timestamp = Timestamp.now(),
        )

        gameRef.update(
            mapOf(
                "rounds.${game.current_round + 1}" to round,
                "current_round" to game.current_round + 1,
            )
        ).await()
    }

    suspend fun refreshGame(): Game {
        checkNotDisposed()
        val g = gameRef.get().await().toObject<Game>()!!
        game = g
        return g
    }

    fun getCurrrentRound(): Round {
        checkNotDisposed()
        val game = currentGame()
        if (game.rounds.isEmpty()) {
            throw IllegalStateException("Game has no rounds")
        }
        return game.rounds[game.current_round.toString()]!!
    }

    suspend fun playHand(hand: Hand) {
        checkNotDisposed()
        val game = refreshGame()
        val me = firebaseRepository.getCurrentUid()
        firebase.gamesCollection.document(gameId)
            .update(mapOf("rounds.${game.current_round}.${me}" to hand))
            .await()
    }

    fun dispose() {
        checkNotDisposed()
        listenerRegistration.remove()
        disposed = true
    }

    fun isGameOver(): Boolean = game?.done == true

    private fun checkNotDisposed() {
        if (disposed) {
            throw IllegalStateException("GameService is disposed")
        }
    }
}