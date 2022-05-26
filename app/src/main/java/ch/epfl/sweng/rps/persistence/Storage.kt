package ch.epfl.sweng.rps.persistence

import android.graphics.Bitmap
import ch.epfl.sweng.rps.models.remote.LeaderBoardInfo
import ch.epfl.sweng.rps.models.remote.User
import ch.epfl.sweng.rps.models.ui.UserStat
import java.io.File

interface Storage {
    fun getFile(file: FILES): File
    fun removeFile(file: FILES): Boolean
    fun getUserDetails(): User?
    fun getStatsData(): List<UserStat>?
    fun getLeaderBoardData(): List<LeaderBoardInfo>?
    fun writeBackUser(user: User)
    fun writeBackStatsData(data: List<UserStat>)
    fun writeBackLeaderBoardData(data: List<LeaderBoardInfo>)
    fun writeBackUserPicture(bitmap: Bitmap)
    fun getUserPicture(): Bitmap?
    enum class FILES(val file: String) {
        USERINFO("USER"),
        LEADERBOARDDATA("LBDATA"),
        USERPICTURE("USERPICTURE"),
        STATSDATA("STATSDATA")
    }
}