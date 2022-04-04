package ch.epfl.sweng.rps.db

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class FirebaseReferences(val env: Env = Env.Prod) {
    val root = FirebaseFirestore.getInstance().document("env/${env.value}")
    private val storageRoot = FirebaseStorage.getInstance().getReference("env/${env.value}")

    fun usersFriendRequestOfUid(uid: String) =
        usersCollection.document(uid).collection("friend_requests")

    val usersCollection = root.collection("users")
    val profilePicturesFolder = storageRoot.child("profile_pictures")

    val gamesCollection = root.collection("games")
}