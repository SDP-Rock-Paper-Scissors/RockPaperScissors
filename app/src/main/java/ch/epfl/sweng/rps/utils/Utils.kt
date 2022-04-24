package ch.epfl.sweng.rps.utils

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

fun consume(block: () -> Any?): () -> Unit = { block() }

data class EmulatorConfig(private val hostname: String, val port: Int) {

    val host get() = hostname.replace("localhost", "10.0.2.2")
}

object FirebaseEmulatorsUtils {
    private var emuUsed_ = false

    val emulatorUsed: Boolean
        get() = emuUsed_

    fun useEmulators(
        firebaseAuthConfig: EmulatorConfig = EmulatorConfig(
            "localhost",
            9099
        ),
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
        FirebaseAuth.getInstance().useEmulator(firebaseAuthConfig.host, firebaseAuthConfig.port)

        Firebase.firestore.firestoreSettings =
            FirebaseFirestoreSettings.Builder()
                .setHost(firestoreConfig.host + ":" + firestoreConfig.port)
                .setSslEnabled(false)
                .build()

        Firebase.europeWest1.useEmulator(firebaseFunctions.host, firebaseFunctions.port)

        FirebaseStorage.getInstance().useEmulator(storageConfig.host, storageConfig.port)

        Log.w("FirebaseEmulatorsUtils", FirebaseFirestore.getInstance().firestoreSettings.host)
        runBlocking {
            Log.w(
                "FirebaseEmulatorsUtils",
                FirebaseFirestore.getInstance().document("global/gamemodes").get()
                    .await().data.toString()
            )
        }

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
private data class Result<T>(val value: T)

val Firebase.europeWest1: FirebaseFunctions
    get() = FirebaseFunctions.getInstance("europe-west1")

val FirebaseFunctions.europeWest1: FirebaseFunctions
    get() = FirebaseFunctions.getInstance("europe-west1")


