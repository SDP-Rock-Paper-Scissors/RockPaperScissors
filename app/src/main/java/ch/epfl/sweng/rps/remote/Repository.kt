package ch.epfl.sweng.rps.remote

import android.graphics.Bitmap
import ch.epfl.sweng.rps.models.remote.User
import ch.epfl.sweng.rps.remote.friends.FriendsRepository
import ch.epfl.sweng.rps.remote.games.GamesRepository
import java.net.URI

/**
 * Repository for the general data of the app, such as the user, the friends, the games, etc.
 */
interface Repository {
    /**
     * Repository for the friends.
     */
    val friends: FriendsRepository

    /**
     * Repository for the games.
     */
    val games: GamesRepository

    /**
     * Returns whether the user is logged in.
     */
    val isLoggedIn get() = rawCurrentUid() != null

    /**
     * Updates the user's profile.
     */
    suspend fun updateUser(vararg pairs: Pair<User.Field, Any>)

    /**
     * Returns the current user's uid or null if the user is not logged in.
     */
    fun rawCurrentUid(): String?

    /**
     * Returns the current user's uid or throws [UserNotLoggedIn] if the user is not logged in.
     */
    fun getCurrentUid() = rawCurrentUid() ?: throw UserNotLoggedIn()

    /**
     * Returns the user with the given uid or null if the user does not exist.
     */
    suspend fun getUser(uid: String): User?

    /**
     * Returns the user's profile picture or null if the user does not have a profile picture.
     */
    suspend fun getUserProfilePictureUrl(uid: String): URI?

    /**
     * Sets the user's profile picture to the given bitmap.
     */
    suspend fun setUserProfilePicture(image: Bitmap, waitForUploadTask: Boolean = false)

    /**
     * Returns the user's profile picture bitmap or null if the user does not have a profile picture.
     */
    suspend fun getUserProfilePictureImage(uid: String): Bitmap?

    /**
     * Creats a new user with the given uid.
     */
    suspend fun createThisUser(name: String?, email: String?): User

    /**
     * Exception thrown when the user is not logged in.
     */
    class UserNotLoggedIn : Exception {
        constructor() : super("User not logged in")
        constructor(uid: String?) : super("User $uid not logged in")
        constructor(cause: Throwable) : super("User not logged in", cause)
        constructor(message: String, cause: Throwable) : super(message, cause)
    }
}