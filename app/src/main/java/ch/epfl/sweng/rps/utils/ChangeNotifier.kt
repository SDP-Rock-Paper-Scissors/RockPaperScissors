package ch.epfl.sweng.rps.utils

import androidx.annotation.CallSuper
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * A class that can be used to notify listeners of changes.
 */
open class ChangeNotifier<T> where  T : ChangeNotifier<T> {
    private val listeners = ArrayList<() -> Unit>()

    /**
     * The number of listeners.
     */
    val listenerCount get() = listeners.size
    private var debugDisposed = false

    /**
     * Notifies all listeners of a change.
     */
    fun notifyListeners() {
        ensureNotDisposed()
        for (n in listeners) {
            try {
                n()
            } catch (e: Exception) {
                throw DispatchingNotificationException(this::class.java, e)
            }
        }
    }

    /**
     * Adds a listener to the list of listeners.
     */
    fun addListener(listener: () -> Unit) {
        ensureNotDisposed()
        listeners.add(listener)
    }

    /**
     * Removes a listener from the list of listeners.
     */
    fun removeListener(listener: () -> Unit) {
        ensureNotDisposed()
        if (!listeners.remove(listener)) {
            throw ListenerNotFoundException(
                "Listener to remove was not found. " +
                        "It is probably already removed or has never been added."
            )
        }
    }




    private fun ensureNotDisposed() {
        if (debugDisposed) {
            throw DisposedException(
                "This ${this::class.java.simpleName} has been disposed and cannot be used anymore."
            )
        }
    }

     /**
     * Disposes of this notifier.
     * This method should be called when this notifier is no longer needed.
     * When overriding this method, call the super method.
     */
    @CallSuper
    open fun dispose() {
        listeners.clear()
        debugDisposed = true
    }

    /**
     * Suspennd function continuing a change is notified and the [predicate] is satisfied.
     */
    suspend fun awaitFor(predicate: (T) -> Boolean) {
        ensureNotDisposed()
        suspendCancellableCoroutine<Unit> {

            val listener = {
                @Suppress("UNCHECKED_CAST")
                if (predicate(this@ChangeNotifier as T)) {
                    it.resume(Unit)
                }
            }
            addListener(listener)
            it.invokeOnCancellation { removeListener(listener) }
        }

    /**
     * An exception thrown when an exception occurs while dispatching notifications.
     */
    class DispatchingNotificationException(
        /**
         * The class of the notifier that threw the exception.
         */
        private val clazz: Class<*>,
        /**
         * The exception that was thrown.
         */
        val exception: Exception
    ) :
        Exception() {

        override val cause: Throwable
            get() = exception

        override val message: String
            get() = "An exception occurred while dispatching notifications: $exception for ${clazz.simpleName}"
    }

    /**
     * An exception thrown when a listener is not found.
     */
    class ListenerNotFoundException : Exception {
        constructor(message: String?, cause: Throwable?) : super(message, cause)
        constructor(message: String) : super(message)
    }

    class DisposedException(message: String) : Exception(message)
}
