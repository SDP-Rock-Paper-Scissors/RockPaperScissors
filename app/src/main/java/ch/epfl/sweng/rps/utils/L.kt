package ch.epfl.sweng.rps.utils

import android.app.Activity
import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.fragment.app.Fragment
import java.util.*

object L {
    private val instances = mutableMapOf<String, LogService>()

    fun of(name: String): LogService = instances.getOrPut(name) { LogService(name) }
    fun of(activity: Activity): LogService = of(activity::class.java)
    fun of(fragment: Fragment): LogService = of(fragment::class.java)
    fun of(clazz: Class<*>): LogService = of(clazz.simpleName)

    fun Log.of(name: String): LogService = L.of(name)
    fun Log.of(activity: Activity): LogService = L.of(activity)
    fun Log.of(fragment: Fragment): LogService = L.of(fragment)
    fun Log.of(clazz: Class<*>): LogService = L.of(clazz)

    // inline fun <reified T> of() = of(T::class.java)

    fun dispose(name: String) {
        instances[name]?.dispose()
        instances.remove(name)
    }

    fun unregister(logService: LogService) {
        instances.remove(logService.name)
    }

    fun unregisterAll() {
        instances.values.forEach { it.dispose() }
        instances.clear()
    }

    enum class Level(val priority: Int) {
        VERBOSE(Log.VERBOSE),
        DEBUG(Log.DEBUG),
        INFO(Log.INFO),
        WARN(Log.WARN),
        ERROR(Log.ERROR),
        ASSERT(Log.ASSERT);
    }

    data class LogEntry(
        val tag: String,
        val message: String,
        val time: Date,
        val level: Level
    )

    class LogService(val name: String) : ChangeNotifier<LogService>() {
        @VisibleForTesting
        val logs = LinkedList<LogEntry>()
        private var logsSize = 100

        var size: Int
            get() = logsSize
            set(value) {
                if (value < 0) {
                    throw IllegalArgumentException("Log size must be positive")
                }
                logsSize = value
                while (logs.size > logsSize) {
                    logs.removeFirst()
                }
                notifyListeners()
            }

        fun log(message: String, level: Level = Level.INFO, throwable: Throwable? = null) {
            val e = LogEntry(name, message, Date(), level)
            val msg = if (throwable != null) {
                message + '\n' + Log.getStackTraceString(throwable)
            } else {
                message
            }
            Log.println(e.level.priority, e.tag, msg)
            logs.add(e)
            if (logs.size > logsSize) {
                logs.removeFirst()
            }
            notifyListeners()
        }

        fun e(message: String, throwable: Throwable? = null) {
            log(message, Level.ERROR, throwable = throwable)
        }

        fun w(message: String) {
            log(message, Level.WARN)
        }

        fun i(message: String) {
            log(message, Level.INFO)
        }

        fun d(message: String) {
            log(message, Level.DEBUG)
        }

        fun v(message: String) {
            log(message, Level.VERBOSE)
        }

        fun clear() {
            logs.clear()
            notifyListeners()
        }
    }
}