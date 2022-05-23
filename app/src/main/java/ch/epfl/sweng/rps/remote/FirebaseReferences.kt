package ch.epfl.sweng.rps.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

/**
 * Instantiating this class necessitates the Firebase SDK to be initialized.
 */
class FirebaseReferences {
    private val root = FirebaseFirestore.getInstance()
    private val storageRoot = FirebaseStorage.getInstance().reference

    val usersFriendRequest = root.collection("friend_requests")

    val usersCollection = root.collection("users")
    val profilePicturesFolder = storageRoot.child("profile_pictures")

    val gamesCollection = root.collection("games")
    val scoresCollection = root.collection("scores")

    fun invitationsOfUid(uid: String) = usersCollection
        .document(uid)
        .collection("invitations")
}

