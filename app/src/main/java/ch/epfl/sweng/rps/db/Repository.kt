package ch.epfl.sweng.rps.db

import android.net.Uri
import ch.epfl.sweng.rps.models.FriendRequest
import ch.epfl.sweng.rps.models.Game
import ch.epfl.sweng.rps.models.Round
import ch.epfl.sweng.rps.models.User

interface Repository {
    suspend fun updateUser(vararg pairs: Pair<User.Field, Any>)
    fun rawCurrentUid(): String?
    fun getCurrentUid() = rawCurrentUid() ?: throw UserNotLoggedIn()
    val isLoggedIn get() = rawCurrentUid() != null

    suspend fun getUser(uid: String): User?

    suspend fun getUserProfilePictureUrl(uid: String): Uri?

    suspend fun createThisUser(name: String?, email: String?): User
    suspend fun sendFriendRequestTo(uid: String)

    suspend fun listFriendRequests(): List<FriendRequest>
    suspend fun getFriends(): List<String>
    suspend fun acceptFriendRequestFrom(userUid: String)
    suspend fun acceptFriendRequestFrom(friendRequest: FriendRequest) =
        acceptFriendRequestFrom(friendRequest.from)

    suspend fun getGame(gameId: String): Game?

    class UserNotLoggedIn : Exception {
        constructor() : super("User not logged in")
        constructor(uid: String?) : super("User $uid not logged in")
        constructor(cause: Throwable) : super("User not logged in", cause)
        constructor(message: String, cause: Throwable) : super(message, cause)
    }
}