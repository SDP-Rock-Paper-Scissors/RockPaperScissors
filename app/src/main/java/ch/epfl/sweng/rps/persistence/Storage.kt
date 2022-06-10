package ch.epfl.sweng.rps.persistence
import android.graphics.Bitmap
import ch.epfl.sweng.rps.models.remote.LeaderBoardInfo
import ch.epfl.sweng.rps.models.remote.User
import ch.epfl.sweng.rps.models.ui.FriendRequestInfo
import ch.epfl.sweng.rps.models.ui.FriendsInfo
import ch.epfl.sweng.rps.models.ui.UserStat
import java.io.File

interface Storage {
    fun removeFile(file : FILES) : Boolean
    fun getUserDetails() : User?
    fun getFriends(): List<FriendsInfo>?
    fun getFriendReqs(): List<FriendRequestInfo>?
    fun getFile(file: FILES): File
    fun deleteFile(file: FILES): Boolean
    fun getUser(): User?
    fun getStatsData(): List<UserStat>?
    fun getLeaderBoardData(): List<LeaderBoardInfo>?
    fun writeBackUser(user: User)
    fun writeBackStatsData(data : List<UserStat>)
    fun writeBackLeaderBoardData(data : List<LeaderBoardInfo>)
    fun writeBackUserPicture(bitmap: Bitmap)
    fun getUserPicture(): Bitmap?
    fun writeBackFriends(friends: List<FriendsInfo>)
    fun writeBackFriendReqs(reqs: List<FriendRequestInfo>)

    enum class FILES(val file:String){
        USERINFO("USER"),
        LEADERBOARDDATA("LBDATA"),
        USERPICTURE("USERPICTURE"),
        STATSDATA("STATSDATA"),
        FRIENDS("FRIENDS"),
        REQUESTS("REQUESTS")
    }

    enum class Origin {
        Cache,
        Remote
    }

    sealed class CacheResult<T> {
        data class Success<T>(val data: T, val origin: Origin) : CacheResult<T>()
        data class Failure<T>(val error: Throwable) : CacheResult<T>()
    }
}