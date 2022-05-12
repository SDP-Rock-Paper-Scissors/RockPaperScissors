package ch.epfl.sweng.rps.persistence
import android.graphics.Bitmap
import ch.epfl.sweng.rps.models.*
import java.io.File

interface Storage {
    fun getFile(file : FILES) : File
    fun removeFile(file : FILES) : Boolean
    fun getUserDetails() : User?
    fun getStatsData() : List<UserStat>?
    fun getLeaderBoardData() : List<LeaderBoardInfo>?
    fun getFriends(): List<FriendsInfo>?
    fun getFriendReqs(): List<FriendRequestInfo>?
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
}