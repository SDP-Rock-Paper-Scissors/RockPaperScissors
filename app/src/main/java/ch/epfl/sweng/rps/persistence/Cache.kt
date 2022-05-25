package ch.epfl.sweng.rps.persistence

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.annotation.VisibleForTesting
import ch.epfl.sweng.rps.models.remote.LeaderBoardInfo
import ch.epfl.sweng.rps.models.remote.User
import ch.epfl.sweng.rps.models.ui.UserStat
import ch.epfl.sweng.rps.remote.FirebaseHelper
import ch.epfl.sweng.rps.remote.FirebaseRepository
import ch.epfl.sweng.rps.remote.Repository
import ch.epfl.sweng.rps.services.ServiceLocator
import java.net.InetAddress


class Cache private constructor(ctx: Context, val preferFresh: Boolean = false) {
    private val storage: Storage = PrivateStorage(ctx)

    private var user: User? = null
    private var userPicture: Bitmap? = null
    private lateinit var userStatData: List<UserStat>
    private lateinit var leaderBoardData: List<LeaderBoardInfo>

    private val repo get() = repoOverride ?: ServiceLocator.getInstance().repository
    private var repoOverride: Repository? = null


    fun getUserDetailsFromCache(): User? {
        if (user != null) return user
        user = storage.getUser()
        return user
    }

    suspend fun getUserDetails(): User? {
        val uid = repo.rawCurrentUid()

        if (uid == null) return getUserDetailsFromCache()

        if (user != null && user!!.uid == uid) return user
        repo.getUser(uid)?.apply {
            user = this
            storage.writeBackUser(this)
            return this
        }
        return user
    }

    suspend fun updateUserDetails(user: User, vararg pairs: Pair<User.Field, Any>) {
        this.user = user
        storage.writeBackUser(user)
        repo.updateUser(*pairs)
    }

    fun setUserDetails(user: User?) {
        if (user == null) {
            this.user = null
            storage.deleteFile(Storage.FILES.USERINFO)
            return
        }
        this.user = user
        storage.writeBackUser(user)
    }


    fun updateStatsData(statsData: List<UserStat>) {
        userStatData = statsData
        storage.writeBackStatsData(statsData)
    }


    fun getUserPicture(): Bitmap? {
        return userPicture ?: storage.getUserPicture()
    }

    suspend fun getUserPictureAsync(): Bitmap? {
        if (!isInternetAvailable()) {
            return getUserPicture()
        }
        if (user == null)
            return null
        userPicture = repo.getUserProfilePictureImage(user!!.uid)
        Log.d("UserPic", userPicture.toString())
        userPicture?.let { storage.writeBackUserPicture(it) }
        return userPicture
    }

    suspend fun updateUserPicture(bitmap: Bitmap) {
        userPicture = bitmap
        repo.setUserProfilePicture(bitmap)
        storage.writeBackUserPicture(bitmap)
    }

    fun getStatsDataFromCache(position: Int): List<UserStat> {
        if (::userStatData.isInitialized) return userStatData
        userStatData = storage.getStatsData() ?: listOf()
        return userStatData
    }

    suspend fun getStatsData(position: Int): List<UserStat> {
        if (!isInternetAvailable()) {
            Log.d("CACHE", "INTERNET NOT AVAILABLE")
            return getStatsDataFromCache(position)
        }
        userStatData = FirebaseHelper.getStatsData(position)
        Log.d("Cache", userStatData.size.toString())
        storage.writeBackStatsData(userStatData)
        return userStatData
    }

    fun updateLeaderBoardData(lBData: List<LeaderBoardInfo>) {
        leaderBoardData = lBData
        storage.writeBackLeaderBoardData(lBData)
    }

    fun getLeaderBoardDataFromCache(position: Int): List<LeaderBoardInfo> {
        if (::leaderBoardData.isInitialized) return leaderBoardData
        leaderBoardData = storage.getLeaderBoardData() ?: listOf()
        return leaderBoardData
    }

    suspend fun getLeaderBoardData(position: Int): List<LeaderBoardInfo> {
        if (!isInternetAvailable()) {
            Log.d("CACHE", "INTERNET NOT AVAILABLE")
            return getLeaderBoardDataFromCache(position)
        }
        leaderBoardData = FirebaseHelper.getLeaderBoard(position)
        Log.d("Cache", leaderBoardData.size.toString())
        storage.writeBackLeaderBoardData(leaderBoardData)
        return leaderBoardData
    }

    private var lastResult = Pair(0L, false)

    private fun isInternetAvailable(): Boolean {
        val now = System.currentTimeMillis()
        if (now - lastResult.first < 1000 && lastResult.second)
            return true
        val res = try {
            val ipAddr: InetAddress = InetAddress.getByName("www.google.com")
            //You can replace it with your name
            !ipAddr.equals("")
        } catch (e: Exception) {
            Log.d("Cache", e.toString())
            false
        }
        lastResult = Pair(now, res)
        return res
    }

    companion object {
        lateinit var cache: Cache

        fun getInstance(): Cache {
            if (!::cache.isInitialized) {
                throw IllegalStateException("Cache not initialized")
            }
            return cache
        }

        fun initialize(ctx: Context): Cache {
            cache = Cache(ctx.applicationContext, true)
            return cache
        }

        @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
        fun initialize(ctx: Context, repository: FirebaseRepository): Cache {
            cache = Cache(ctx.applicationContext)
            cache.repoOverride = repository
            return cache
        }
    }
}