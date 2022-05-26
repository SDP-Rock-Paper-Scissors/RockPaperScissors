package ch.epfl.sweng.rps.utils

import androidx.annotation.CallSuper
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

open class ChangeNotifier<T> where  T : ChangeNotifier<T> {
    private val listeners = ArrayList<() -> Unit>()
    val listenerCount get() = listeners.size

    fun notifyListeners() {
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
        listeners.add(listener)
    }

    fun removeListener(listener: () -> Unit) {
        if (!listeners.remove(listener)) {
            throw ListenerNotFoundException(
                "Listener to remove was not found. " +
                        "It is probably already removed or has never been added."
            )
        }
    }

    @CallSuper
    open fun dispose() {
        listeners.clear()
    }

    @Suppress("UNCHECKED_CAST")
    suspend fun awaitFor(predicate: (T) -> Boolean) =
        suspendCancellableCoroutine<Unit> { cont ->
            val listener = {
                if (predicate(this@ChangeNotifier as T)) {
                    cont.resume(Unit)
                }
            }
            addListener(listener)
            cont.invokeOnCancellation {
                removeListener(listener)
            }
        }

    class ListenerException : Exception {
        constructor(message: String?, cause: Throwable?) : super(message, cause)
        constructor(message: String) : super(message)
    }

    class ListenerNotFoundException : Exception {
        constructor(message: String?, cause: Throwable?) : super(message, cause)
        constructor(message: String) : super(message)
    }
}
