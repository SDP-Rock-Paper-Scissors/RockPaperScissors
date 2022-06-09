package ch.epfl.sweng.rps.utils

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.View
import androidx.core.content.FileProvider
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File
import java.net.InetSocketAddress
import java.net.Socket


fun consume(block: () -> Any?): () -> Unit = { block() }

data class EmulatorConfig(private val hostname: String, val port: Int) {

    val host get() = hostname.replace("localhost", "10.0.2.2")
}

object FirebaseEmulatorsUtils {
    private var emuUsed_ = false

    val emulatorUsed: Boolean
        get() = emuUsed_

    fun useEmulators(
        firebaseAuthConfig: EmulatorConfig? = null /*EmulatorConfig(
            "localhost",
            9099
        )*/,
        firestoreConfig: EmulatorConfig = EmulatorConfig(
            "localhost",
            8080
        ),
        storageConfig: EmulatorConfig = EmulatorConfig(
            "localhost",
            9199
        ),
        firebaseFunctions: EmulatorConfig = EmulatorConfig(
            "localhost",
            5001
        )
    ) {
        Log.w("FirebaseEmulatorsUtils", FirebaseFirestore.getInstance().firestoreSettings.host)

        firebaseAuthConfig?.let {
            FirebaseAuth.getInstance().useEmulator(it.host, it.port)
        }

        Firebase.firestore.firestoreSettings =
            FirebaseFirestoreSettings.Builder()
                .setHost(firestoreConfig.host + ":" + firestoreConfig.port)
                .setSslEnabled(false)
                .setPersistenceEnabled(false)
                .build()

        Firebase.europeWest1.useEmulator(firebaseFunctions.host, firebaseFunctions.port)

        Firebase.storage.useEmulator(storageConfig.host, storageConfig.port)

        Log.w("FirebaseEmulatorsUtils", Firebase.firestore.firestoreSettings.host)

        emuUsed_ = true
    }
}

suspend fun <T> retry(times: Int = 3, delayMs: Long = 500, block: suspend () -> T): T {
    var exception: Exception? = null
    for (attempt in 1..times) {
        try {
            return block()
        } catch (e: Exception) {
            exception = e
            if (attempt < times)
                delay(delayMs)
        }
    }
    throw RetryException("Retry failed after $times attempts", exception)
}

class RetryException(message: String, cause: Throwable? = null) : Exception(message, cause)

val Firebase.europeWest1: FirebaseFunctions
    get() = FirebaseFunctions.getInstance("europe-west1")

inline fun <reified T> List<DocumentSnapshot>.toListOf(): List<T> =
    map { it.toObject(T::class.java)!! }

inline fun <reified T> List<DocumentSnapshot>.toListOfNullable(): List<T?> =
    map { it.toObject(T::class.java) }


@Suppress("BlockingMethodInNonBlockingContext")
suspend fun isInternetAvailable(): Boolean = withContext(Dispatchers.IO) {
    try {
        val socket = Socket()
        socket.connect(InetSocketAddress("1.1.1.1", 53))
        socket.close()
        true
    } catch (e: Exception) {
        L.of("isInternetAvailable")
            .e("Got a ${e::class.java.simpleName}, assuming no internet connection", e)
        false
    }
}


sealed class SuspendResult<T> {
    data class Success<T>(val value: T) : SuspendResult<T>()
    data class Failure<T>(val error: Throwable) : SuspendResult<T>()

    val asData
        get() = when (this) {
            is Success -> this
            is Failure -> null
        }

    @Throws(Throwable::class)
    fun getOrThrow(): T {
        when (this) {
            is Success -> return value
            is Failure -> throw error
        }
    }

    suspend fun <R> whenIs(
        success: suspend (Success<T>) -> R,
        failure: suspend (Failure<T>) -> R
    ): R =
        when (this) {
            is Success -> success(this)
            is Failure -> failure(this)
        }

    suspend fun <R> then(block: suspend (T) -> R): SuspendResult<R> = when (this) {
        is Success -> guard { block(value) }
        is Failure -> Failure(error)
    }

    companion object {
        suspend fun <R> guard(block: suspend () -> R): SuspendResult<R> {
            return try {
                Success(block())
            } catch (e: Throwable) {
                Failure(e)
            }
        }


        fun <T, R> showSnackbar(
            context: Context,
            view: View,
            duration: Int = Snackbar.LENGTH_LONG,
            callback: BaseTransientBottomBar.BaseCallback<Snackbar>? = null,
            block: suspend (Failure<T>) -> R,
        ): suspend (Failure<T>) -> R {
            return { f ->
                val s = Snackbar
                    .make(view, f.error.message ?: "", duration)
                    .setTextColor(Color.RED)
                    .setAction("Open details") {
                        runBlocking {
                            val file = dumpDebugInfos(context, f.error)
                            openJsonFile(context, file)
                        }
                    }
                callback?.let { s.addCallback(it) }
                s.show()
                block(f)
            }
        }

        fun <T, R> showSnackbar(
            activity: Activity,
            duration: Int = Snackbar.LENGTH_LONG,
            callback: BaseTransientBottomBar.BaseCallback<Snackbar>? = null,
            block: suspend (Failure<T>) -> R,
        ): suspend (Failure<T>) -> R = showSnackbar(
            activity,
            activity.findViewById(android.R.id.content),
            duration,
            callback = callback,
            block = block
        )

    }
}

suspend fun <T> SuspendResult<T>.showSnackbarIfError(
    activity: Activity,
    callback: BaseTransientBottomBar.BaseCallback<Snackbar>? = null
): SuspendResult<T> {
    return whenIs(
        success = { it },
        failure = SuspendResult.showSnackbar(activity, callback = callback) { it }
    )
}

suspend fun <T> SuspendResult<T>.whenOrSnackbar(
    activity: Activity,
    callback: BaseTransientBottomBar.BaseCallback<Snackbar>? = null,
    success: suspend (T) -> Unit,
) {
    whenIs(
        success = { success(it.value) },
        failure = SuspendResult.showSnackbar(activity, callback = callback) {}
    )
}


suspend fun <T> guardSuspendable(block: suspend () -> T): SuspendResult<T> =
    SuspendResult.guard(block)

@Suppress("BlockingMethodInNonBlockingContext", "VisibleForTests")
suspend fun dumpDebugInfos(
    context: Context,
    error: Throwable? = null
): File = withContext(Dispatchers.IO) {
    val outputDir =
        context.getExternalFilesDir("external_files")
    val outputFile = File.createTempFile("dump_${System.currentTimeMillis()}", ".json", outputDir)
    val m = mutableMapOf<String, Any>(
        "logs" to L.allInstances().map { (name, log) ->
            mapOf(
                "tag" to name,
                "logs" to log.logs.map { e ->
                    mapOf(
                        "text" to "${e.time}: [${e.level}] ${e.message}",
                        *(e.throwable?.let {
                            listOf(
                                "throwable" to mapOf(
                                    "stackTrace" to it.stackTrace.map { it.toString() },
                                    "throwable_name" to it.message,
                                    "throwable_type" to it.javaClass.simpleName
                                )
                            )
                        } ?: listOf<Pair<String, Any>>()).toTypedArray()
                    )
                }
            )
        }
    )
    if (error != null) {
        m["error"] = mapOf(
            "error" to mapOf(
                "message" to error.message,
                "stackTrace" to error.stackTrace.map { it.toString() },
                "name" to error.javaClass.simpleName
            ),
        )
    }
    Log.d("writeErrorDebugFile", m.toString())

    val gson = GsonBuilder().serializeNulls().setPrettyPrinting().create()
    val text = gson.toJson(m)
    outputFile.writeText(text)
    outputFile
}

fun openJsonFile(context: Context, file: File) {
    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        file
    )
    val jsonIntent = Intent(Intent.ACTION_VIEW)
    jsonIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    jsonIntent.setDataAndType(uri, "application/json")

    for (type in listOf("application/json", "text/plain")) {
        jsonIntent.setDataAndType(uri, type)
        try {
            return context.startActivity(jsonIntent)
        } catch (e: ActivityNotFoundException) {
            continue
        }
    }
    throw ActivityNotFoundException("No activity found to open file $uri")
}

sealed class Option<T> {
    class Some<T>(val value: T) : Option<T>()
    class None<T>() : Option<T>()

    companion object {
        fun <T> fromNullable(value: T?): Option<T> = value?.let { Some(it) } ?: None()
    }
}



