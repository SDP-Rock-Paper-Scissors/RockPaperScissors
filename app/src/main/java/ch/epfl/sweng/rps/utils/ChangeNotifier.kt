package ch.epfl.sweng.rps.utils

import androidx.annotation.CallSuper

open class ChangeNotifier {

    private val listeners = ArrayList<() -> Unit>()

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


    val listenerCount get() = listeners.size

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


    class ListenerException : Exception {
        constructor(message: String?, cause: Throwable?) : super(message, cause)
        constructor(message: String) : super(message)
    }


    class ListenerNotFoundException : Exception {
        constructor(message: String?, cause: Throwable?) : super(message, cause)
        constructor(message: String) : super(message)
    }
}
