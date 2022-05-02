package ch.epfl.sweng.rps.db

import android.graphics.Bitmap
import ch.epfl.sweng.rps.models.*
import java.net.URI

interface Repository {
    suspend fun updateUser(vararg pairs: Pair<User.Field, Any>)
    fun rawCurrentUid(): String?
    fun getCurrentUid() = rawCurrentUid() ?: throw UserNotLoggedIn()
    val isLoggedIn get() = rawCurrentUid() != null

    suspend fun getUser(uid: String): User?

    suspend fun getUserProfilePictureUrl(uid:String): URI?
    suspend fun setUserProfilePicture(image : Bitmap)
    suspend fun getUserProfilePictureImage(uid:String): Bitmap?

    suspend fun createThisUser(name: String?, email: String?): User
    suspend fun sendFriendRequestTo(uid: String)

    suspend fun listFriendRequests(): List<FriendRequest>
    suspend fun getFriends(): List<String>
    suspend fun changeFriendRequestToStatus(userUid: String, status: FriendRequest.Status)
    suspend fun acceptFriendRequest(userUid: String) =
        changeFriendRequestToStatus(userUid, FriendRequest.Status.ACCEPTED)

    suspend fun rejectFriendRequest(userUid: String) =
        changeFriendRequestToStatus(userUid, FriendRequest.Status.REJECTED)

    suspend fun getGame(gameId: String): Game?
    suspend fun getLeaderBoardScore(): List<TotalScore>
    suspend fun gamesOfUser(uid: String): List<Game>
    suspend fun myActiveGames(): List<Game>

    suspend fun statsOfUser(uid: String): UserStats

    suspend fun listInvitations(): List<Invitation>

    class UserNotLoggedIn : Exception {
        constructor() : super("User not logged in")
        constructor(uid: String?) : super("User $uid not logged in")
        constructor(cause: Throwable) : super("User not logged in", cause)
        constructor(message: String, cause: Throwable) : super(message, cause)
    }
}