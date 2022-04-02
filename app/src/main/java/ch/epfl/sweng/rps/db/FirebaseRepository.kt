package ch.epfl.sweng.rps.db

import android.net.Uri
import ch.epfl.sweng.rps.models.FriendRequest
import ch.epfl.sweng.rps.models.Game
import ch.epfl.sweng.rps.models.Round
import ch.epfl.sweng.rps.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await


class FirebaseRepository(
    private val firebase: FirebaseReferences = FirebaseReferences(Env.Prod)
) : Repository {

    override suspend fun updateUser(vararg pairs: Pair<User.Field, Any>) {
        val arguments = FirebaseHelper.processUserArguments(*pairs)

        val uid = getCurrentUid()
        firebase.usersCollection.document(uid).update(arguments).await()
    }

    override suspend fun getUser(uid: String): User {
        val user = firebase.usersCollection.document(uid).get().await()
        return user.toObject<User>()!!
    }

    override suspend fun getUserProfilePictureUrl(uid: String): Uri? {
        return if (getUser(uid).hasProfilePhoto)
            firebase.profilePicturesFolder.child(uid).downloadUrl.await()
        else
            null
    }

    override suspend fun createUser(name: String, email: String?) {
        val uid = getCurrentUid()
        firebase.usersCollection.document(uid).set(
            FirebaseHelper.userFrom(uid, name, email)
        ).await()
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

    override suspend fun getRoundsOfGame(gameId: String): List<Round> {
        return firebase.roundsOfGame(gameId).get().await().documents.map {
            it.toObject<Round>()!!
        }
    }

    suspend fun clearDevEnv() {
        if (firebase.env != Env.Dev) {
            throw UnsupportedOperationException("This method is only available in a dev environment")
        }
        firebase.root.delete().await()
    }
}

