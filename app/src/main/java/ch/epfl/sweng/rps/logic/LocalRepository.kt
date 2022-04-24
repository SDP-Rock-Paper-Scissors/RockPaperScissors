package ch.epfl.sweng.rps.logic

import ch.epfl.sweng.rps.models.FriendRequest
import ch.epfl.sweng.rps.models.Game
import ch.epfl.sweng.rps.models.Invitation
import ch.epfl.sweng.rps.models.User
import com.google.firebase.Timestamp
import java.net.URI

class LocalRepository(private var uid: String? = null) : Repository {

    fun setCurrentUid(newUid: String?) {
        uid = newUid
    }

    val users = mutableMapOf<String, User>()

    private val friendRequests = mutableMapOf<String, MutableMap<String, FriendRequest>>()

    override suspend fun updateUser(vararg pairs: Pair<User.Field, Any>) {
        var user = getUser(getCurrentUid())
        pairs.forEach {
            user = when (it.first) {
                User.Field.EMAIL -> user.copy(email = it.second as String)
                User.Field.USERNAME -> user.copy(username = it.second as String)
                User.Field.GAMES_HISTORY_PRIVACY -> user.copy(games_history_privacy = it.second as String)
                User.Field.HAS_PROFILE_PHOTO -> user.copy(has_profile_photo = it.second as Boolean)
                User.Field.UID -> throw IllegalArgumentException("Cannot change uid")
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

    override suspend fun getUserProfilePictureUrl(uid: String): URI? {
        val cond = getUser(getCurrentUid()).has_profile_photo
        return if (cond) {
            URI("https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_960_720.png")
        } else {
            null
        }
    }

    override suspend fun createThisUser(name: String?, email: String?): User {
        val user = FirebaseHelper.userFrom(
            uid = getCurrentUid(),
            name = name.orEmpty(),
            email = email
        )
        users[user.uid] = user
        return user
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

    val games = mutableMapOf<String, Game>()

    override suspend fun getGame(gameId: String): Game? {
        return games[gameId]
    }

    override suspend fun gamesOfUser(uid: String): List<Game> {
        return games.values.filter { uid in it.players }
    }

    override suspend fun myActiveGames(): List<Game> {
        return games.values.filter { it.players.contains(getCurrentUid()) && !it.done }
    }

    private var invitations = mutableMapOf<String, Invitation>()

    override suspend fun listInvitations(): List<Invitation> {
        return invitations.values.toList()
    }

}