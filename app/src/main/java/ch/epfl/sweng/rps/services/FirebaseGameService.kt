package ch.epfl.sweng.rps.services

import android.util.Log
import androidx.annotation.VisibleForTesting
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
) : GameService() {
    private var _disposed = false
    private val gameRef = firebase.gamesCollection.document(gameId)
    private var listenerRegistration: ListenerRegistration? = null
    private var _active = false

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun setGameTest(game: Game) {
        super.game = game
    }

    override fun startListening(): FirebaseGameService {
        checkNotDisposed()
        if (listenerRegistration != null) {
            throw GameServiceException("Listener already registered")
        }
        listenerRegistration =
            gameRef.addSnapshotListener { value, e ->
                if (e != null) {
                    Log.e("FirebaseGameService", "Error while listening to game $gameId", e)
                    error = e
                } else {
                    game = value?.toObject<Game>()
                }
            }
        _active = true
        return this
    }

    override fun stopListening() {
        checkNotDisposed()
        listenerRegistration?.remove()
        _active = false
    }


    override val currentGame: Game
        get() {
            checkNotDisposed()
            if (game == null) {
                throw error ?: GameServiceException("Game service has not received a game yet")
            } else {
                return game!!
            }
        }

    override val isGameFull: Boolean
        get() {
            return currentGame.players.size == currentGame.mode.playerCount
        }

    /**
     * Because it updates the data in this database,
     * it is not sure that the [currentGame] will up to date after this call.
     *
     * You should still use wait for the [currentGame] to be updated.
     */
    override suspend fun addRound(): Round {
        checkNotDisposed()
        val game = refreshGame()
        if (game.players.first() != firebaseRepository.getCurrentUid()) {
            throw GameServiceException("Only the first player can add a round")
        }
        val round = Round(
            hands = mutableMapOf(),
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
                throw GameServiceException("Game has no rounds")
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

        listenerRegistration?.remove()

        _disposed = true
        super.dispose()
    }

    override val isGameOver: Boolean get() = game?.done ?: false
    override val isDisposed: Boolean get() = _disposed
    override val active: Boolean
        get() = _active

    private fun checkNotDisposed() {
        if (_disposed) {
            throw GameServiceException("GameService is disposed")
        }
    }
}