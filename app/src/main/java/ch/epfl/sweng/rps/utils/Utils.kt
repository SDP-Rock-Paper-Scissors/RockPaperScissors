@file:Suppress("unused")

package ch.epfl.sweng.rps.utils

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.delay
import java.net.InetAddress

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

fun isInternetAvailable(): Boolean {
    val res = try {
        val ipAddr: InetAddress = InetAddress.getByName("www.google.com")
        //You can replace it with your name
        !ipAddr.equals("")
    } catch (e: Exception) {
        Log.d("Cache", e.toString())
        false
    }
    return res
}
