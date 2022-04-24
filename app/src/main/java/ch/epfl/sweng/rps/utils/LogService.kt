package ch.epfl.sweng.rps.utils

import android.util.Log
import java.util.*

object LogService : ChangeNotifier() {

    private val logEntries = LinkedList<LogEntry>()
    val logs: List<LogEntry>
        get() = logEntries
    private var size_ = 100
    var size: Int
        get() = size_
        set(value) {
            if (value < 0) {
                throw IllegalArgumentException("Log size must be positive")
            }
            size_ = value
            while (logEntries.size > size_) {
                logEntries.removeFirst()
            }
            notifyListeners()
        }

    fun log(tag: String, message: String, level: Level = Level.INFO) {
        val entry = LogEntry(tag, message, Date(), level)
        Log.println(level.intValue, tag, message)
        logEntries.add(entry)
        if (logEntries.size > size) {
            logEntries.removeFirst()
        }
        notifyListeners()
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
        logEntries.clear()
        notifyListeners()
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
}