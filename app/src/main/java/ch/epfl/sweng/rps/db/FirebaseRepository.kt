package ch.epfl.sweng.rps.db

import android.net.Uri
import android.util.Log
import ch.epfl.sweng.rps.models.FriendRequest
import ch.epfl.sweng.rps.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await


open class FirebaseRepository(env: Env = Env.PROD) : Repository, FirebaseReferences(env) {
    override suspend fun updateUser(vararg pairs: Pair<User.Field, Any>) {
        val arguments = FirebaseHelper.processUserArguments(*pairs)
        val uid = getCurrentUid()
        usersCollection.document(uid).update(arguments).await()
    }


    override suspend fun getUser(uid: String): User? {
        val user = usersCollection.document(uid).get().await() ?: return null
        return user.toObject<User>()
    }

    override suspend fun getUserProfilePictureUrl(uid: String): Uri? {
        return if (getUser(uid)?.hasProfilePhoto == true)
            profilePicturesFolder.child(uid).downloadUrl.await()
        else
            null
    }

    override suspend fun createUser(name: String?,  email: String?) {
        val uid = getCurrentUid()
        usersCollection.document(uid).set(
            FirebaseHelper.userFrom(uid, name.orEmpty(), email)
        ).await()
    }

    override fun rawCurrentUid(): String? = FirebaseAuth.getInstance().currentUser?.uid

    override suspend fun sendFriendRequestTo(uid: String) {
        usersFriendRequestOfUid(uid)
            .add(FriendRequest(from = getCurrentUid())).await()
    }

    override suspend fun listFriendRequests(): List<FriendRequest> {
        return usersFriendRequestOfUid(getCurrentUid()).get().await().documents.map {
            it.toObject<FriendRequest>()!!
        }
    }

    override suspend fun getFriends(): List<String> {
        return usersFriendRequestOfUid(getCurrentUid())
            .whereEqualTo("accepted", true)
            .get().await().documents
            .map { it.toObject<FriendRequest>()!!.from }
    }

    override suspend fun acceptFriendRequest(userUid: String) {
        usersFriendRequestOfUid(userUid)
            .whereEqualTo("from", getCurrentUid()).limit(1)
            .get().await().documents.first().reference.update("accepted", true).await()
    }

    suspend fun clearDevEnv() {
        if (env != Env.DEV) {
            throw UnsupportedOperationException("This method is only available in a dev environment")
        }
        root.delete().await()
    }

}

