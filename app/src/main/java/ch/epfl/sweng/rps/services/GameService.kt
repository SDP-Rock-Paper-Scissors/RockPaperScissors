package ch.epfl.sweng.rps.services

import androidx.annotation.CallSuper
import ch.epfl.sweng.rps.models.Game
import ch.epfl.sweng.rps.models.Hand
import ch.epfl.sweng.rps.models.Round
import ch.epfl.sweng.rps.utils.ChangeNotifier
import ch.epfl.sweng.rps.utils.StateNotifier


/**
 * The GameService is the interface for the game logic.
 * You can listen to the changes of the game state by using [addListener].
 * You can listen to errors by using [addErrorListener].
 *
 * This class needs to be disposed when you don't need it anymore.
 */
abstract class GameService : ChangeNotifier<GameService>() {
    abstract val gameId: String

    /**
     * Starts the service.
     * This should be called only once.
     *
     * In [FirebaseGameService] this listens to the game document.
     */
    abstract fun startListening(): GameService
    abstract fun stopListening()


    abstract val isGameFull: Boolean

    abstract suspend fun addRound(): Round

    private var _game: Game? = null
    protected var game: Game?
        get() = _game
        set(value) {
            if (value != _game) {
                _game = value
                notifyListeners()
            }
        }
    abstract val currentGame: Game
    abstract suspend fun refreshGame(): Game

    abstract val currentRound: Round

    abstract suspend fun playHand(hand: Hand)

    @CallSuper
    override fun dispose() {
        _game = null
        _error.dispose()
        super.dispose()
    }

    abstract val isGameOver: Boolean

    abstract val isDisposed: Boolean
    abstract val active: Boolean


    fun addErrorListener(listener: () -> Unit) {
        _error.addListener(listener)
    }


    fun removeErrorListener(listener: () -> Unit) {
        _error.removeListener(listener)
    }

    private val _error = StateNotifier<Exception?>(null)
    var error: Exception?
        get() = _error.value
        set(value) {
            if (value != _error.value) {
                _error.value = value
                notifyListeners()
            }
        }

    val owner: String
        get() = currentGame.players.first()

    class GameServiceException : Exception {
        constructor(message: String) : super(message)
        constructor(message: String, cause: Throwable) : super(message, cause)
        constructor(cause: Throwable) : super(cause)
    }
}