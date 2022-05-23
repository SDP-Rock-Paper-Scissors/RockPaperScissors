package ch.epfl.sweng.rps.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

/**
 * Instantiating this class necessitates the Firebase SDK to be initialized.
 */
class FirebaseReferences {
    private val root = FirebaseFirestore.getInstance()
    private val storageRoot = FirebaseStorage.getInstance().reference

    /**
     * This is the friend request collection.
     */
    val usersFriendRequest = root.collection("friend_requests")

    /**
     * This is the users collection.
     */
    val usersCollection = root.collection("users")

    /**
     * This is the profile pictures folder.
     */
    val profilePicturesFolder = storageRoot.child("profile_pictures")

    /**
     * This is the games collection.
     */
    val gamesCollection = root.collection("games")

    /**
     * This is the scores collection.
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

