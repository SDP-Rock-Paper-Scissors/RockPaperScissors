package ch.epfl.sweng.rps.utils

import android.util.Log
import androidx.annotation.VisibleForTesting
import java.util.*

object LogService {
    private val logService = LogService()
    val notifier: LogService get() = logService

    @VisibleForTesting
    val logs
        get() = logService.logs

    var size: Int
        get() = logService.size
        set(value) {
            logService.size = value
        }

    fun log(tag: String, message: String, level: Level = Level.INFO) {
        val entry = LogEntry(tag, message, Date(), level)
        Log.println(level.intValue, tag, message)
        logService.add(entry)
    }

    fun e(tag: String, message: String) {
        log(tag, message, Level.ERROR)
    }

    fun w(tag: String, message: String) {
        log(tag, message, Level.WARN)
    }

    fun i(tag: String, message: String) {
        log(tag, message, Level.INFO)
    }

    fun d(tag: String, message: String) {
        log(tag, message, Level.DEBUG)
    }

    fun v(tag: String, message: String) {
        log(tag, message, Level.VERBOSE)
    }

    fun clear() {
        logService.clear()
    }

    enum class Level(val intValue: Int) {
        VERBOSE(2),
        DEBUG(3),
        INFO(4),
        WARN(5),
        ERROR(6),
        ASSERT(7)
    }

    data class LogEntry(
        val tag: String,
        val message: String,
        val time: Date,
        val level: Level
    )

    class LogService : ChangeNotifier<LogService>() {
        @VisibleForTesting
        val logs = LinkedList<LogEntry>()
        private var size_ = 100
        var size: Int
            get() = size_
            set(value) {
                if (value < 0) {
                    throw IllegalArgumentException("Log size must be positive")
                }
                size_ = value
                while (logs.size > size_) {
                    logs.removeFirst()
                }
                notifyListeners()
            }

        fun add(entry: LogEntry) {
            logs.add(entry)
            if (logs.size > size_) {
                logs.removeFirst()
            }
            notifyListeners()
        }

        fun clear() {
            logs.clear()
            notifyListeners()
        }
    }
}