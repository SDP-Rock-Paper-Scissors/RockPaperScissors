package ch.epfl.sweng.rps.utils

import androidx.annotation.CallSuper
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

open class ChangeNotifier<T> where  T : ChangeNotifier<T> {
    private val listeners = ArrayList<() -> Unit>()
    val listenerCount get() = listeners.size
    private var debugDisposed = false

    fun notifyListeners() {
        ensureNotDisposed()
        for (n in listeners) {
            try {
                n()
            } catch (e: Exception) {
                throw ListenerException(
                    "Error while dispatching notifications of ${this::class.java.simpleName}",
                    e
                )
            }
        }
    }

    fun addListener(listener: () -> Unit) {
        ensureNotDisposed()
        listeners.add(listener)
    }

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

    @CallSuper
    open fun dispose() {
        listeners.clear()
        debugDisposed = true
    }

    suspend fun awaitFor(predicate: (T) -> Boolean) {
        ensureNotDisposed()
        suspendCancellableCoroutine<Unit> {
            @Suppress("UNCHECKED_CAST")
            val listener = {
                if (predicate(this@ChangeNotifier as T)) {
                    it.resume(Unit)
                }
            }
            addListener(listener)
            it.invokeOnCancellation { removeListener(listener) }
        }
    }

    class ListenerException(message: String?, cause: Throwable?) : Exception(message, cause)

    class ListenerNotFoundException(message: String) : Exception(message)

    class DisposedException(message: String) : Exception(message)
}
