package ch.epfl.sweng.rps.db

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

open class FirebaseReferences(val env: Env = Env.PROD) {
    protected val root: DocumentReference = FirebaseFirestore.getInstance().document("env/$env")
    private val storageRoot = FirebaseStorage.getInstance().getReference("env/$env")

    fun usersFriendRequestOfUid(uid: String): CollectionReference =
        usersCollection.document(uid).collection("friend_requests")

    val usersCollection = root.collection("users")
    val profilePicturesFolder = storageRoot.child("profile_pictures")
}