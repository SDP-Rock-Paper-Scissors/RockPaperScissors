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
import ch.epfl.sweng.rps.utils.SuspendResult
import ch.epfl.sweng.rps.utils.guardSuspendable
import ch.epfl.sweng.rps.utils.isInternetAvailable

/**
 * This class is to be used as the main reference for all data operations.
 * The cache implements two methods for each data that you want to get:
 * Async and non-async methods.
 * Async methods are to be used when you specifically want up to date data, and
 * non-async when you want whatever data is already available either in cache or local storage.
 * Every method that fetches from a remote repository such as firebase always updates cache and
 * local storage.
 */
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

    /**
     * Returns the user data from the cache.
     */
    fun getUserDetailsFromCache(): User? {
        if (user != null) return user
        user = storage.getUser()
        return user
    }

    private val log = L.of("RPSCache")

    /**
     * This function returns the details of the user from the cache or from the remote repository.
     */
    suspend fun getUserDetails(): SuspendResult<User?> {
        val uid = repo.rawCurrentUid()
        if (uid == null) {
            log.d("getUserDetails: no user logged in")
            return SuspendResult.Success(getUserDetailsFromCache())
        }

        return if (user != null && user!!.uid == uid)
            SuspendResult.Success(getUserDetailsFromCache())
        else
            repo.getUser(uid).then { u ->
                u?.let {
                    user = it
                    storage.writeBackUser(it)
                    it
                }
            }
    }

    /**
     * This functions updates both user stored in the local storage and in firebase
     * with the data passed as parameter, see parameters for more details.
     * @param user The user to be updated
     */
    fun setUserDetails(user: User?) {
        if (user == null) {
            this.user = null
            storage.deleteFile(Storage.FILES.USERINFO)
            return
        }
        this.user = user
        storage.writeBackUser(user)
    }

    /**
     * Updates the stats data in both firebase and local storage.
     * @param statsData The stats data to be updated
     */
    fun updateStatsData(statsData: List<UserStat>) {
        userStatData = statsData
        storage.writeBackStatsData(statsData)
    }


    /**
     * Returns the user picture of the current user from either cache or local storage.
     * @return The user picture gotten from storage/cache.
     */
    fun getUserPicture(): Bitmap? {
        return userPicture ?: storage.getUserPicture()
    }

    /**
     * Returns the user picture from firebase if available and automatically updates cache and
     * storage.
     * @return The user picture gotten from Firebase.
     */
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

    /**
     * Updates the user picture in firebase, cache and local storage.
     * @param bitmap The new user picture
     */
    suspend fun updateUserPicture(bitmap: Bitmap) {
        userPicture = bitmap
        repo.setUserProfilePicture(bitmap)
        storage.writeBackUserPicture(bitmap)
    }

    /**
     * Retrieves the stats data from cache or local storage.
     * @return the stats data for the user.
     */
    fun getStatsDataFromCache(): List<UserStat> {
        if (userStatData != null) return userStatData!!
        userStatData = storage.getStatsData() ?: listOf()
        return userStatData!!
    }

    /**
     * Retrieves the stats data from cache or local storage.
     * @param position the position of the stats.
     * @return the stats data for the user.
     */
    suspend fun getStatsData(position: Int): SuspendResult<List<UserStat>> {
        return SuspendResult.Failure(Exception("Not implemented"))
        return if (!isInternetAvailable()) {
            log.d("INTERNET NOT AVAILABLE")
            guardSuspendable { getStatsDataFromCache() }
        } else {
            guardSuspendable { FirebaseHelper.getStatsData(position) }.whenIs(
                success = { (data) ->
                    log.d("getStatsData: got " + data.size.toString() + " stats")
                    storage.writeBackStatsData(data)
                    userStatData = data
                    SuspendResult.Success(data)
                },
                failure = {
                    log.e("FAILED TO GET STATS DATA", it.error)
                    it
                }
            )
        }
    }

    /**
     * MAINLY FOR TESTING
     * Updates the leaderboard data in cache and localStorage
     * @param lBData the new leaderboard data
     */
    fun updateLeaderBoardData(lBData: List<LeaderBoardInfo>) {
        leaderBoardData = lBData
        storage.writeBackLeaderBoardData(lBData)
    }

    /**
     * Gets leaderboard data from cache or local storage
     * @return A list of LeaderBoardInfo fetched from local storage or cache.
     */
    fun getLeaderBoardDataFromCache(): List<LeaderBoardInfo> {
        if (leaderBoardData != null) return leaderBoardData!!
        leaderBoardData = storage.getLeaderBoardData() ?: listOf()
        return leaderBoardData!!
    }

    /**
     * Gets leaderboard data firebase.
     * @param position the position of the leaderboard to load.
     * @return A list of LeaderBoardInfo fetched from firebase.
     */
    suspend fun getLeaderBoardData(position: Int): SuspendResult<List<LeaderBoardInfo>> {
        if (!isInternetAvailable()) {
            log.d("INTERNET NOT AVAILABLE")
            return guardSuspendable { getLeaderBoardDataFromCache() }
        } else {
            return guardSuspendable { FirebaseHelper.getLeaderBoard(position) }.whenIs(
                success = { (data) ->
                    log.d("getLeaderBoardData: got " + data.size.toString() + " leaderboard")
                    storage.writeBackLeaderBoardData(data)
                    leaderBoardData = data
                    SuspendResult.Success(data)
                },
                failure = {
                    log.e("FAILED TO GET LEADERBOARD DATA", it.error)
                    it
                }
            )
        }
    }

    /**
     * Clears the cache and local storage.
     */
    fun clear() {
        clearLocalVars()
        for (file in Storage.FILES.values()) {
            storage.deleteFile(file)
        }
    }

    companion object {
        lateinit var cache: Cache

        /**
         * Returns the instance of the cache.
         */
        fun getInstance(): Cache {
            if (!::cache.isInitialized) {
                throw IllegalStateException("Cache not initialized")
            }
            return cache
        }

        /**
         * Initializes the cache. It needs a context to initialize the storage.
         */
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