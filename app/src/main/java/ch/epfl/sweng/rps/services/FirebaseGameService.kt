package ch.epfl.sweng.rps.services

import ch.epfl.sweng.rps.db.FirebaseReferences
import ch.epfl.sweng.rps.db.FirebaseRepository
import ch.epfl.sweng.rps.models.Game
import ch.epfl.sweng.rps.models.Hand
import ch.epfl.sweng.rps.models.Round
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await

class FirebaseGameService(
    private val firebase: FirebaseReferences,
    private val firebaseRepository: FirebaseRepository,
    override val gameId: String,
) : GameService {
    private var game: Game? = null
    private var disposed = false
    private var gameRef = firebase.gamesCollection.document(gameId)
    private lateinit var listenerRegistration: ListenerRegistration

    override fun startListening(): FirebaseGameService {
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

    override fun isServiceReady(): Boolean {
        return game != null
    }

    override val currentGame: Game
        get() {
            checkNotDisposed()
            if (!isServiceReady()) {
                throw IllegalStateException("Game service has not received a game yet")
            }
            return game!!
        }

    override val isGameFull: Boolean
        get() {
            return currentGame.players.size == currentGame.mode.playerCount
        }

    override suspend fun addRound(): Round {
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
        return round
    }

    override suspend fun refreshGame(): Game {
        checkNotDisposed()
        val g = gameRef.get().await().toObject<Game>()!!
        game = g
        return g
    }

    override val currentRound: Round
        get() {
            checkNotDisposed()
            val game = currentGame
            if (game.rounds.isEmpty()) {
                throw IllegalStateException("Game has no rounds")
            }
            return game.rounds[game.current_round.toString()]!!
        }

    override suspend fun playHand(hand: Hand) {
        checkNotDisposed()
        val game = refreshGame()
        val me = firebaseRepository.getCurrentUid()
        firebase.gamesCollection.document(gameId)
            .update(mapOf("rounds.${game.current_round}.${me}" to hand))
            .await()
    }

    override fun dispose() {
        checkNotDisposed()
        listenerRegistration.remove()
        disposed = true
    }

    override val isGameOver: Boolean get() = game?.done == true

    private fun checkNotDisposed() {
        if (disposed) {
            throw IllegalStateException("GameService is disposed")
        }
    }
}