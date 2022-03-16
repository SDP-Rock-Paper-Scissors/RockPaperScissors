package ch.epfl.sweng.rps.db

import android.net.Uri
import ch.epfl.sweng.rps.models.User

interface Repository {
    suspend fun updateUser(vararg pairs: Pair<User.Field, Any>): Unit
    fun rawCurrentUid(): String?
    fun getCurrentUid() = rawCurrentUid() ?: throw RepositoryException.UserNotLoggedIn()
    val isLoggedIn get() = rawCurrentUid() != null

    suspend fun getUser(uid: String): User

    suspend fun getUserProfilePictureUrl(uid: String): Uri?

    suspend fun createUser(name: String, email: String?): Unit
    suspend fun addFriend(uid: String)


    class NotLoggedInException
}