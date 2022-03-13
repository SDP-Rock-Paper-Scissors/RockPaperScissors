package ch.epfl.sweng.rps.db

import android.net.Uri
import ch.epfl.sweng.rps.models.FriendRequest
import ch.epfl.sweng.rps.models.User

interface Repository {
    suspend fun updateUser(vararg pairs: Pair<User.Field, Any>): Unit
    fun rawCurrentUid(): String?
    fun getCurrentUid() = rawCurrentUid() ?: throw RepositoryException.UserNotLoggedIn()
    val isLoggedIn get() = rawCurrentUid() != null

    suspend fun getUser(uid: String): User

    suspend fun getUserProfilePictureUrl(uid: String): Uri?

    suspend fun createUser(name: String, email: String?): Unit
    suspend fun sendFriendRequest(uid: String)

    suspend fun listFriendRequests(): List<FriendRequest>
    suspend fun getFriends(): List<String>
    suspend fun acceptFriendRequest(userUid: String)
    suspend fun acceptFriendRequest(friendRequest: FriendRequest) =
        acceptFriendRequest(friendRequest.from)
}