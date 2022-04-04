package ch.epfl.sweng.rps.db

import android.net.Uri
import ch.epfl.sweng.rps.models.FriendRequest
import ch.epfl.sweng.rps.models.Game
import ch.epfl.sweng.rps.models.User
import com.google.firebase.Timestamp

class LocalRepository(private var uid: String? = null) : Repository {

    fun setCurrentUid(newUid: String?) {
        uid = newUid
    }

    private val users = mutableMapOf<String, User>()

    private val friendRequests = mutableMapOf<String, MutableMap<String, FriendRequest>>()

    @Suppress("UNCHECKED_CAST")
    override suspend fun updateUser(vararg pairs: Pair<User.Field, Any>) {
        var user = getUser(getCurrentUid())
        pairs.forEach {
            user = when (it.first) {
                User.Field.EMAIL -> user.copy(email = it.second as String)
                User.Field.USERNAME -> user.copy(username = it.second as String)
                User.Field.GAMES_HISTORY_PRIVACY -> user.copy(gamesHistoryPrivacy = it.second as String)
                User.Field.HAS_PROFILE_PHOTO -> user.copy(hasProfilePhoto = it.second as Boolean)
                User.Field.UID -> user.copy(uid = it.second as String)
                User.Field.MATCHESLIST -> user.copy(matchesList = it.second as List<String>)
            }
        }
        users[getCurrentUid()] = user
    }

    override fun rawCurrentUid(): String? {
        return uid
    }

    override suspend fun getUser(uid: String): User {
        return users[uid]!!
    }

    override suspend fun getUserProfilePictureUrl(uid: String): Uri? {
        val cond = getUser(getCurrentUid()).hasProfilePhoto
        return if (cond) {
            Uri.parse("https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_960_720.png")
        } else {
            null
        }
    }

    override suspend fun createUser(name: String?, email: String?) {
        val user = FirebaseHelper.userFrom(
            uid = getCurrentUid(),
            name = name.orEmpty(),
            email = email
        )
        users[user.uid] = user
    }

    override suspend fun sendFriendRequestTo(uid: String) {
        val map = (friendRequests[uid] ?: mutableMapOf())
        map[getCurrentUid()] = FriendRequest(getCurrentUid(), Timestamp.now())
        friendRequests[uid] = map
    }

    override suspend fun listFriendRequests(): List<FriendRequest> {
        return friendRequests[getCurrentUid()]?.entries?.map { it.value } ?: emptyList()
    }

    override suspend fun getFriends(): List<String> {
        return friendRequests[getCurrentUid()]
            ?.filter { it.value.accepted }
            ?.map { it.value.from }
            ?.toList() ?: emptyList()
    }

    override suspend fun acceptFriendRequestFrom(userUid: String) {
        friendRequests[getCurrentUid()]?.set(
            userUid,
            friendRequests[getCurrentUid()]!![userUid]!!.copy(accepted = true)
        )
    }

    override suspend fun getGame(gameId: String): Game? {
        TODO("Not yet implemented")
    }

}