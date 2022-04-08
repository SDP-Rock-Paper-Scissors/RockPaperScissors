package ch.epfl.sweng.rps.db

import android.net.Uri
import ch.epfl.sweng.rps.models.FriendRequest
import ch.epfl.sweng.rps.models.Game
import ch.epfl.sweng.rps.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await
import java.net.URI


class FirebaseRepository(
    private val firebase: FirebaseReferences = FirebaseReferences()
) : Repository {

    override suspend fun updateUser(vararg pairs: Pair<User.Field, Any>) {
        val arguments = FirebaseHelper.processUserArguments(*pairs)
        val uid = getCurrentUid()
        firebase.usersCollection.document(uid).update(arguments).await()
    }

    override suspend fun getUser(uid: String): User? {
        val user = firebase.usersCollection.document(uid).get().await()
        return user?.toObject<User>()
    }

    override suspend fun getUserProfilePictureUrl(uid: String): URI? {
        return if (getUser(uid)!!.has_profile_photo)
            firebase.profilePicturesFolder.child(uid).downloadUrl.await().toURI()
        else
            null
    }

    override suspend fun createThisUser(name: String?, email: String?): User {
        val uid = getCurrentUid()
        val user = FirebaseHelper.userFrom(uid, name, email)
        firebase.usersCollection.document(uid).set(user).await()
        return user
    }

    override fun rawCurrentUid(): String? = FirebaseAuth.getInstance().currentUser?.uid

    override suspend fun sendFriendRequestTo(uid: String) {
        firebase.usersFriendRequestOfUid(uid)
            .add(FriendRequest(from = getCurrentUid())).await()
    }

    override suspend fun listFriendRequests(): List<FriendRequest> {
        return firebase.usersFriendRequestOfUid(getCurrentUid()).get().await().documents.map {
            it.toObject<FriendRequest>()!!
        }
    }

    override suspend fun getFriends(): List<String> {
        return firebase.usersFriendRequestOfUid(getCurrentUid())
            .whereEqualTo("accepted", true)
            .get().await().documents
            .map { it.toObject<FriendRequest>()!!.from }
    }

    override suspend fun acceptFriendRequestFrom(userUid: String) {
        firebase.usersFriendRequestOfUid(userUid)
            .whereEqualTo("from", getCurrentUid()).limit(1)
            .get().await().documents.first().reference.update("accepted", true).await()
    }

    override suspend fun getGame(gameId: String): Game? {
        return firebase.gamesCollection.document(gameId).get().await().toObject<Game>()
    }

    override suspend fun gamesOfUser(uid: String): List<Game> {
        return firebase.gamesCollection.whereArrayContains("players", uid).get()
            .await().documents.map {
                it.toObject<Game>()!!
            }
    }

    private fun Uri.toURI(): URI = URI(toString())
}


