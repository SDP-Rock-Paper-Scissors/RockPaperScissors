package ch.epfl.sweng.rps.db

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

interface FirebaseReferences {
    fun usersFriendRequestOfUid(uid: String): CollectionReference =
        usersCollection.document(uid).collection("friend_requests")

    val usersCollection
        get() = FirebaseFirestore.getInstance().collection("users")
    val profilePicturesFolder
        get() = FirebaseStorage.getInstance().getReference("profile_pictures")
}