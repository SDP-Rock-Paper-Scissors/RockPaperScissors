package ch.epfl.sweng.rps.db

import android.net.Uri
import ch.epfl.sweng.rps.models.FriendRequest
import ch.epfl.sweng.rps.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await


open class FirestoreRepository : Repository, FirebaseReferences {
    override suspend fun updateUser(vararg pairs: Pair<User.Field, Any>) {
        val arguments = FirebaseHelper.processUserArguments(*pairs)

        val uid = getCurrentUid()
        usersCollection.document(uid).update(arguments).await()
    }


    override suspend fun getUser(uid: String): User {
        val user = usersCollection.document(uid).get().await()
        return user.toObject<User>()!!
    }

    override suspend fun getUserProfilePictureUrl(uid: String): Uri? {
        return if (getUser(uid).hasProfilePhoto)
            profilePicturesFolder.child(uid).downloadUrl.await()
        else
            null
    }

    override suspend fun createUser(name: String, email: String?) {
        val uid = getCurrentUid()
        usersCollection.document(uid).set(
            User(
                email = email,
                username = name,
                friends = listOf(),
                gamesHistoryPrivacy = User.Privacy.PUBLIC,
                hasProfilePhoto = false,
                uid = uid
            )
        ).await()
    }

    override fun rawCurrentUid(): String? = FirebaseAuth.getInstance().currentUser?.uid

    override suspend fun addFriend(uid: String) {
        usersFriendRequestOfUid(uid)
            .add(FriendRequest(from = getCurrentUid())).await()
    }

}

