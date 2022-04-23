package ch.epfl.sweng.rps.db

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

/**
 * Instantiating this class necessitates the Firebase SDK to be initialized.
 */
class FirebaseReferences {
    val root = FirebaseFirestore.getInstance()
    val storageRoot = FirebaseStorage.getInstance().reference

    fun usersFriendRequestOfUid(uid: String) =
        usersCollection.document(uid).collection("friend_requests")

    val usersCollection = root.collection("users")
    val profilePicturesFolder = storageRoot.child("profile_pictures")

    val gamesCollection = root.collection("games")
}

