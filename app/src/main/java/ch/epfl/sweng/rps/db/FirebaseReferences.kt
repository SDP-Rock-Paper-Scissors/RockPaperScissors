package ch.epfl.sweng.rps.db

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

/**
 * Instantiating this class necessitates the Firebase SDK to be initialized.
 */
class FirebaseReferences {
    val root = FirebaseFirestore.getInstance()
    val storageRoot = FirebaseStorage.getInstance().reference

    /**
     * Thefriend request collection.
     */
    val usersFriendRequest = root.collection("friend_requests")

    /**
     * Theusers collection.
     */
    val usersCollection = root.collection("users")

    /**
     * Theprofile pictures folder.
     */
    val profilePicturesFolder = storageRoot.child("profile_pictures")

    /**
     * Thegames collection.
     */
    val gamesCollection = root.collection("games")

    /**
     * Thescores collection.
     */
    val scoresCollection = root.collection("scores")

    /**
     * Returns the collection of invitations for the given user
     * with [uid].
     */
    fun invitationsOfUid(uid: String) = usersCollection
        .document(uid)
        .collection("invitations")
}

