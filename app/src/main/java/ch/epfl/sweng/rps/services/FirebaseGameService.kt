package ch.epfl.sweng.rps.services

import android.util.Log
import androidx.annotation.VisibleForTesting
import ch.epfl.sweng.rps.db.FirebaseReferences
import ch.epfl.sweng.rps.db.FirebaseRepository
import ch.epfl.sweng.rps.models.remote.Game
import ch.epfl.sweng.rps.models.remote.Game.Companion.toGame
import ch.epfl.sweng.rps.models.remote.Hand
import ch.epfl.sweng.rps.models.remote.Round
import ch.epfl.sweng.rps.utils.L
import ch.epfl.sweng.rps.utils.consume
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await

class FirebaseGameService(
    private val firebase: FirebaseReferences,
    private val firebaseRepository: FirebaseRepository,
    override val gameId: String,
) : GameService() {
    private var _disposed = false
    private val gameRef = firebase.gamesCollection.document(gameId)
    private var listenerRegistration: ListenerRegistration? = null

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun setGameTest(game: Game) {
        super.game = game
    }

    override fun startListening(): FirebaseGameService {
        checkNotDisposed()
        if (listenerRegistration != null) {
            L.of(this::class.java).w("Listener already registered")
            return this
        }
        listenerRegistration =
            gameRef.addSnapshotListener { value, e ->
                if (e != null) {
                    Log.e("FirebaseGameService", "Error while listening to game $gameId", e)
                    error = e
                } else {
                    if (value?.exists() == true) {
                        game = value.toGame()
                    } else {
                        Log.e("FirebaseGameService", "Game $gameId does not exist")
                        error = IllegalStateException("Game $gameId does not exist")
                    }
                }
            }
        return this
    }

    override fun stopListening() {
        checkNotDisposed()
        listenerRegistration?.remove()
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
            return currentGame.players.size == currentGame.gameMode.playerCount
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
        val round = Round.Rps(
            hands = mutableMapOf(),
            timestamp = Timestamp.now(),
        )

        gameRef.update(
            mapOf(
                "${Game.FIELDS.ROUNDS}.${game.current_round + 1}" to round,
                Game.FIELDS.CURRENT_ROUND to game.current_round + 1,
            )
        ).await()
        return round
    }

    override suspend fun refreshGame(): Game {
        checkNotDisposed()
        val g = gameRef.get().await().toGame()!!
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
            .update(mapOf("${Game.FIELDS.ROUNDS}.${game.current_round}.hands.${me}" to hand))
            .await()
    }

    override fun dispose() {
        checkNotDisposed()

        listenerRegistration?.remove()

        _disposed = true
        super.dispose()
    }

    sealed class PlayerCount(val playerCount: Int) {
        class Some(playerCount: Int) : PlayerCount(playerCount)
        class Full(playerCount: Int) : PlayerCount(playerCount)
    }

    suspend fun opponentCount(): Flow<PlayerCount> = callbackFlow {
        if (isGameFull) {
            send(PlayerCount.Full(currentGame.players.size))
            channel.close()
        } else {
            send(PlayerCount.Some(currentGame.players.size))
            val cb = consume {
                if (isGameFull) {
                    trySendBlocking(PlayerCount.Full(currentGame.players.size))
                    channel.close()
                } else {
                    trySendBlocking(PlayerCount.Some(currentGame.players.size))
                }
            }
            addListener(cb)
            awaitClose { removeListener(cb) }
        }
    }

    val isListening get() = listenerRegistration != null

    override val isGameOver: Boolean get() = game?.done ?: false
    override val isDisposed: Boolean get() = _disposed
    override val started: Boolean
        get() = game?.started ?: false

    private fun checkNotDisposed() {
        if (_disposed) {
            throw GameServiceException("GameService is disposed")
        }
    }

    suspend fun awaitForGame() {
        return awaitFor { game != null }
    }

    override val imTheOwner get() = game?.players?.first() == firebaseRepository.getCurrentUid()

    suspend fun waitForGameStart(): Boolean {
        if (imTheOwner) {
            firebase.gamesCollection.document(gameId).update(mapOf(Game.FIELDS.STARTED to true))
                .await()
            return true
        }
        if (started) {
            return true
        }
        return suspendCancellableCoroutine { continuation ->
            val cb = {
                if (started) {
                    continuation.resumeWith(Result.success(true))
                }
            }
            addListener(cb)
            continuation.invokeOnCancellation { removeListener(cb) }
        }
    }

    //two below functions don't work for me as expected, either I use them in a wrong way or there is a bug
    override suspend fun awaitForAllHands() {
        awaitFor { currentRound.hands.size == 2 }// 2 is the number of players, for now hardcoded
    }

    /**
     * Utility function to wait for the owner of the game to add a round.
     * (it's needed because of the previous design decisions - only owner can add a round)
     */
    override suspend fun awaitForRoundAdded() {
        awaitFor { imTheOwner || currentRound.hands.size == 1 }
    }
}
