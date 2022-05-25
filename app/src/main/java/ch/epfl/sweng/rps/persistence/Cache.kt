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
import ch.epfl.sweng.rps.utils.L
import ch.epfl.sweng.rps.utils.isInternetAvailable


class Cache private constructor(ctx: Context, val preferFresh: Boolean = false) {
    private val storage: Storage = PrivateStorage(ctx)

    private var user: User? = null
    private var userPicture: Bitmap? = null
    private var userStatData: List<UserStat>? = null
    private var leaderBoardData: List<LeaderBoardInfo>? = null

    private val repo get() = repoOverride ?: ServiceLocator.getInstance().repository
    private var repoOverride: Repository? = null

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun clearLocalVars() {
        user = null
        userPicture = null
        userStatData = null
        leaderBoardData = null
    }


    fun getUserDetailsFromCache(): User? {
        if (user != null) return user
        user = storage.getUser()
        return user
    }

    val log = L.of("RPSCache")

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
        if (userStatData != null) return userStatData!!
        userStatData = storage.getStatsData() ?: listOf()
        return userStatData!!
    }

    suspend fun getStatsData(position: Int): List<UserStat> {
        if (!isInternetAvailable()) {
            log.d("INTERNET NOT AVAILABLE")
            return getStatsDataFromCache(position)
        }
        userStatData = FirebaseHelper.getStatsData(position)
        log.d(userStatData!!.size.toString())
        storage.writeBackStatsData(userStatData!!)
        return userStatData!!
    }

    fun updateLeaderBoardData(lBData: List<LeaderBoardInfo>) {
        leaderBoardData = lBData
        storage.writeBackLeaderBoardData(lBData)
    }

    fun getLeaderBoardDataFromCache(position: Int): List<LeaderBoardInfo> {
        if (leaderBoardData != null) return leaderBoardData!!
        leaderBoardData = storage.getLeaderBoardData() ?: listOf()
        return leaderBoardData!!
    }

    suspend fun getLeaderBoardData(position: Int): List<LeaderBoardInfo> {
        if (!isInternetAvailable()) {
            log.d("INTERNET NOT AVAILABLE")
            return getLeaderBoardDataFromCache(position)
        }
        leaderBoardData = FirebaseHelper.getLeaderBoard(position)
        log.d(leaderBoardData!!.size.toString())
        storage.writeBackLeaderBoardData(leaderBoardData!!)
        return leaderBoardData!!
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