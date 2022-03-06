package ch.epfl.sweng.rps.db

import android.net.Uri
import ch.epfl.sweng.rps.db.interfaces.Database
import ch.epfl.sweng.rps.models.User
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage


open class FirestoreDatabase : Database {
    override fun updateUser(vararg pairs: Pair<User.Field, Any>): Task<Void> {
        val uid = getCurrentUid() ?: throw Exception("Not logged in.")

        return usersCollection.document(uid)
            .update(pairs.associate { t -> Pair(t.first.field, t.second) })
    }

    internal val usersCollection
        get() = FirebaseFirestore.getInstance().collection("users")
    private val profilePicturesFolder
        get() = FirebaseStorage.getInstance().getReference("profile_pictures")

    override fun getUser(uid: String): Task<User> {
        return usersCollection.document(uid).get()
            .continueWith { task ->
                task.result?.toObject<User>()
                    ?: throw Exception("No document found for uid $uid")
            }
    }

    override fun getUserProfilePicture(uid: String): Task<Uri?> =
        getUser(uid).continueWithTask { task ->
            if (task.result?.hasProfilePhoto == true)
                profilePicturesFolder.child(uid).downloadUrl
            else
                Tasks.forResult(null)
        }

    override fun createUser(name: String, email: String?): Task<Void> {
        val uid = getCurrentUid()!!
        return usersCollection.document(uid).set(
            User(
                email = email,
                username = name,
                friends = listOf(),
                gamesHistoryPublic = true,
                hasProfilePhoto = false,
                uid = getCurrentUid()!!
            )
        )
    }

    override fun getCurrentUid(): String? = FirebaseAuth.getInstance().currentUser?.uid
}

