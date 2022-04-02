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
    private val gameId: String,
    val listen: Boolean = true
) {
    private var game: Game? = null
    private var disposed = false
    private var gameRef = firebase.gamesCollection.document(gameId)
    private lateinit var listenerRegistration: ListenerRegistration

    fun start(): GameService {
        checkNotDisposed()
        if (listen) {
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
        val game = currentGame()
        val uuid = UUID.randomUUID().toString()
        val round = Round(
            id = uuid,
            hands = emptyMap(),
            timestamp = Timestamp.now(),
            game_id = gameId
        )

        gameRef.update(
            mapOf(
                "rounds" to arrayUnion(uuid)
            )
        ).await()
        firebase.roundsOfGame(gameId).document(uuid).set(round).await()
    }

    suspend fun refreshGame() {
        checkNotDisposed()
        game = gameRef.get().await().toObject<Game>()
    }

    suspend fun getCurrrentRound(): Round {
        checkNotDisposed()
        val game = currentGame()
        if (game.rounds.isEmpty()) {
            throw IllegalStateException("Game has no rounds")
        }
        val roundId = game.rounds.last()
        return firebase.roundsOfGame(gameId).document(roundId).get().await().toObject<Round>()!!
    }

    suspend fun playHand(hand: Hand) {
        checkNotDisposed()
        val round = getCurrrentRound()
        val me = firebaseRepository.getCurrentUid()
            ?: throw IllegalStateException("User is not logged in")
        val handMap = round.hands.toMutableMap()
        handMap[me] = hand
        firebase.roundsOfGame(gameId).document(round.id).update(mapOf("hands" to handMap)).await()
    }

    fun dispose() {
        checkNotDisposed()
        listenerRegistration.remove()
        disposed = true
    }

    fun isGameOver(): Boolean {
        return game?.done == true
    }

    fun getGameId(): String {
        return gameId
    }

    private fun checkNotDisposed() {
        if (disposed) {
            throw IllegalStateException("GameService is disposed")
        }
    }


}