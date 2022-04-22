package ch.epfl.sweng.rps.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

fun consume(block: () -> Any?): () -> Unit = { block() }

data class EmulatorConfig(private val hostname: String, val port: Int) {

    val host get() = hostname.replace("localhost", "10.0.2.2")
}

object FirebaseEmulatorsUtils {
    private var emuUsed_ = false

    val emulatorUsed: Boolean
        get() = emuUsed_

    fun useEmulators(
        firebaseAuthConfig: EmulatorConfig? = EmulatorConfig(
            "localhost",
            9099
        ),
        firestoreConfig: EmulatorConfig? = EmulatorConfig(
            "localhost",
            8080
        ),
        storageConfig: EmulatorConfig? = EmulatorConfig(
            "localhost",
            9199
        )
    ) {
        if (firebaseAuthConfig != null) {
            FirebaseAuth.getInstance().useEmulator(firebaseAuthConfig.host, firebaseAuthConfig.port)
        }
        if (firestoreConfig != null) {
            FirebaseFirestore.getInstance().useEmulator(firestoreConfig.host, firestoreConfig.port)
        }
        if (storageConfig != null) {
            FirebaseStorage.getInstance().useEmulator(storageConfig.host, storageConfig.port)
        }
        emuUsed_ = true
    }
}

