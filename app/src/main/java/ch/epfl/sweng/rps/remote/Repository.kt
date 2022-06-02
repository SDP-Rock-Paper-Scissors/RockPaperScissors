package ch.epfl.sweng.rps.remote

import android.graphics.Bitmap
import ch.epfl.sweng.rps.models.remote.User
import ch.epfl.sweng.rps.remote.friends.FriendsRepository
import ch.epfl.sweng.rps.remote.games.GamesRepository
import ch.epfl.sweng.rps.utils.SuspendResult
import java.net.URI

interface Repository {
    val friends: FriendsRepository
    val games: GamesRepository
    val isLoggedIn get() = rawCurrentUid() != null

    suspend fun updateUser(vararg pairs: Pair<User.Field, Any>): SuspendResult<Unit>
    fun rawCurrentUid(): String?
    fun getCurrentUid() = rawCurrentUid() ?: throw UserNotLoggedIn()
    suspend fun getUser(uid: String): SuspendResult<User?>
    suspend fun getUserProfilePictureUrl(uid: String): SuspendResult<URI?>
    suspend fun setUserProfilePicture(image: Bitmap, waitForUploadTask: Boolean = false): SuspendResult<Unit>
    suspend fun getUserProfilePictureImage(uid: String): Bitmap?
    suspend fun createThisUser(name: String?, email: String?): SuspendResult<User>
    class UserNotLoggedIn : Exception {
        constructor() : super("User not logged in")
        constructor(uid: String?) : super("User $uid not logged in")
        constructor(cause: Throwable) : super("User not logged in", cause)
        constructor(message: String, cause: Throwable) : super(message, cause)
    }
}