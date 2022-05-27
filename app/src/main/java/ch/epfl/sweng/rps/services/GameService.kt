package ch.epfl.sweng.rps.services

import androidx.annotation.CallSuper
import ch.epfl.sweng.rps.models.remote.Game
import ch.epfl.sweng.rps.models.remote.Hand
import ch.epfl.sweng.rps.models.remote.Round
import ch.epfl.sweng.rps.utils.ChangeNotifier
import ch.epfl.sweng.rps.utils.StateNotifier


/**
 * The GameService is the interface for the game db.
 * You can listen to the changes of the game state by using [addListener].
 * You can listen to errors by using [addErrorListener].
 *
 * This class needs to be disposed when you don't need it anymore.
 */
abstract class GameService : ChangeNotifier<GameService>() {
    /**
     * The game id of the current game.
     */
    abstract val gameId: String

    /**
     * Whether the game is full or not.
     */
    abstract val isGameFull: Boolean
    private var _game: Game? = null


    /**
     * The current game.
     */
    protected var game: Game?
        get() = _game
        set(value) {
            if (value != _game) {
                _game = value
                notifyListeners()
            }
        }

    /**
     * The current game. Throws an exception if the game is null.
     */
    abstract val currentGame: Game

    /**
     * The current round.
     */
    abstract val currentRound: Round

    /**
     * Whether the game is over or not.
     */
    abstract val isGameOver: Boolean

    /**
     * Whether this [GameService] is disposed or not.
     */
    abstract val isDisposed: Boolean

    /**
     * Whether the game started or not.
     */
    abstract val started: Boolean
    private val _error = StateNotifier<Exception?>(null)

    /**
     * The last error that happened if any.
     */
    var error: Exception?
        get() = _error.value
        set(value) {
            if (value != _error.value) {
                _error.value = value
                notifyListeners()
            }
        }

    /**
     * The host of the game.The first player in the player list.
     */
    val host: String
        get() = currentGame.players.first()

    /**
     * Whther the current player is the host or not.
     */
    abstract val amITheHost: Boolean

    /**
     * Starts the service.
     * This should be called only once.
     *
     * In [FirebaseGameService] this listens to the game document.
     */
    abstract fun startListening(): GameService

    /**
     * Stops litening to the game document.
     */
    abstract fun stopListening()

    /**
     * Adds a new round to the game.
     */
    abstract suspend fun addRound(): Round

    /**
     * Refreshes the game.
     */
    abstract suspend fun refreshGame(): Game

    /**
     * Plays the given [hand] in the current round.
     */
    abstract suspend fun playHand(hand: Hand)

    @CallSuper
    override fun dispose() {
        _game = null
        _error.dispose()
        super.dispose()
    }

    /**
     * Adds a new error listener.
     */
    fun addErrorListener(listener: () -> Unit) {
        _error.addListener(listener)
    }

    /**
     * Removes the given error listener.
     */
    fun removeErrorListener(listener: () -> Unit) {
        _error.removeListener(listener)
    }

    /**
     * Awaits for all hands in the current round to be played.
     */
    abstract suspend fun awaitForAllHands()

    /**
     * Awaits for the current round to be over.
     */
    abstract suspend fun awaitForRoundAdded()

    /**
     * Exception thrown by this service.
     */
    class GameServiceException : Exception {
        constructor(message: String) : super(message)
        constructor(message: String, cause: Throwable) : super(message, cause)
        constructor(cause: Throwable) : super(cause)
    }
}