package ch.epfl.sweng.rps.remote

import android.graphics.Bitmap
import androidx.annotation.VisibleForTesting
import ch.epfl.sweng.rps.models.remote.*
import ch.epfl.sweng.rps.remote.friends.FriendsRepository
import ch.epfl.sweng.rps.remote.games.GamesRepository
import ch.epfl.sweng.rps.utils.SuspendResult
import ch.epfl.sweng.rps.utils.guardSuspendable
import com.google.firebase.Timestamp
import java.net.URI

/**
 * This class is a local implementation of the [Repository] interface.
 *
 * It implements the [FriendsRepository] as well as the [GamesRepository] interface.
 * It can thus be used to access friends and games data.
 */
class LocalRepository(private var uid: String? = null) : Repository, GamesRepository,
    FriendsRepository {

    @VisibleForTesting
    internal val users = mutableMapOf<String, User>()
    private val friendRequests = mutableMapOf<String, MutableMap<String, FriendRequest>>()
    override val friends: FriendsRepository
        get() = this
    override val games: GamesRepository
        get() = this
    internal val gamesMap = mutableMapOf<String, Game>()
    internal var leaderBoardScore = mutableListOf<TotalScore>()

    @VisibleForTesting
    internal val invitations = mutableMapOf<String, Invitation>()
    internal fun setCurrentUid(newUid: String?) {
        uid = newUid
    }

    override suspend fun updateUser(vararg pairs: Pair<User.Field, Any>): SuspendResult<Unit> {
        return getUser(getCurrentUid()).then {
            var user = it!!
            pairs.forEach { p ->
                user = when (p.first) {
                    User.Field.EMAIL -> user.copy(email = p.second as String)
                    User.Field.USERNAME -> user.copy(username = p.second as String)
                    User.Field.GAMES_HISTORY_PRIVACY -> user.copy(games_history_privacy = p.second as String)
                    User.Field.HAS_PROFILE_PHOTO -> user.copy(has_profile_photo = p.second as Boolean)
                    User.Field.UID -> throw IllegalArgumentException("Cannot change uid")
                }
            }
            users[getCurrentUid()] = user
        }
    }

    override fun rawCurrentUid(): String? {
        return uid
    }

    override suspend fun getUser(uid: String) = guardSuspendable {
        users[uid]
    }

    override suspend fun getUserProfilePictureUrl(uid: String) = guardSuspendable {
        val cond = getUser(uid).asData?.value?.has_profile_photo ?: false
        if (cond) {
            URI("https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_960_720.png")
        } else {
            null
        }
    }

    override suspend fun createThisUser(name: String?, email: String?) = guardSuspendable {
        val user = FirebaseHelper.userFrom(
            uid = getCurrentUid(),
            name = name.orEmpty(),
            email = email
        )
        users[user.uid] = user
        user
    }

    override suspend fun sendFriendRequestTo(uid: String) {
        val fr = FriendRequest.build(getCurrentUid(), uid, Timestamp.now())
        friendRequests.add(fr)
    }

    override suspend fun listFriendRequests(): List<FriendRequest> {
        return friendRequests
    }

    override suspend fun getFriends(): List<String> {
        return friendRequests.filter { it.status == FriendRequest.Status.ACCEPTED }
            .map { it.users.first { it != getCurrentUid() } }
    }

    override suspend fun changeFriendRequestToStatus(
        userUid: String,
        status: FriendRequest.Status
    ) {
        val i = friendRequests.indexOfFirst { it.users.contains(userUid) }
        friendRequests[i] = friendRequests[i].copy(status = status)
    }

    val games = mutableMapOf<String, Game>()

    override suspend fun getGame(gameId: String): Game? {
        return games[gameId]
    }

    var leaderBoardScore = mutableListOf<TotalScore>()
    override suspend fun getLeaderBoardScore(scoreMode:String): List<TotalScore> {
        return leaderBoardScore
    }


    override suspend fun gamesOfUser(uid: String): List<Game> {
        return games.values.filter { uid in it.players }
    }

    override suspend fun myActiveGames(): List<Game> {
        return games.values.filter { it.players.contains(getCurrentUid()) && !it.done }
    }

    override suspend fun statsOfUser(uid: String): UserStats {
        return UserStats(
            total_games = gamesOfUser(uid).size,
            wins = 0,
            userId = uid
        )
    }

    @VisibleForTesting
    val invitations = mutableMapOf<String, Invitation>()

    override suspend fun listInvitations(): List<Invitation> {
        return invitations.values.toList()
    }

    override suspend fun setUserProfilePicture(image: Bitmap, waitForUploadTask: Boolean) =
        guardSuspendable {}

    override suspend fun getUserProfilePictureImage(uid: String): Bitmap? {
        return null
    }
}