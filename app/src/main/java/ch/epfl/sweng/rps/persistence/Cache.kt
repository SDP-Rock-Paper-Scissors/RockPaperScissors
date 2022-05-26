package ch.epfl.sweng.rps.persistence

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import ch.epfl.sweng.rps.models.remote.LeaderBoardInfo
import ch.epfl.sweng.rps.models.remote.User
import ch.epfl.sweng.rps.models.ui.UserStat
import ch.epfl.sweng.rps.remote.FirebaseHelper
import ch.epfl.sweng.rps.remote.FirebaseRepository
import ch.epfl.sweng.rps.services.ServiceLocator
import java.net.InetAddress

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


    private var fbRepo = ServiceLocator.getInstance().repository
    private val storage: Storage = PrivateStorage(ctx)
    private var user: User? = null
    private var userPicture: Bitmap? = null
    private lateinit var userStatData: List<UserStat>
    private lateinit var leaderBoardData: List<LeaderBoardInfo>

    /**
     * This function returns the user details by fetching them from
     * the cache or the local storage if not in memory already.
     * @return The user.
     */
    fun getUserDetails(): User? {
        if (user != null) return user
        user = storage.getUserDetails()
        return user
    }

    /**
     * This function accepts a callback which passes as parameter the User object for
     * the currently logged user retrieved from firebase.
     * @param callback The callback to be called when fetching is complete
     */
    suspend fun getUserDetailsAsync(callback: (User?) -> Unit) {
        if (!isInternetAvailable())
            return
        val uid = fbRepo.getCurrentUid()
        user = fbRepo.getUser(uid)
        callback(user)
    }

    /**
     * This functions updates both user stored in the local storage and in firebase
     * with the data passed as parameter, see parameters for more details.
     * @param user The user to be updated
     * @param pairs The field of the user that have to be changed
     */
    suspend fun updateUserDetails(user: User, vararg pairs: Pair<User.Field, Any>) {
        this.user = user
        storage.writeBackUser(user)
        fbRepo.updateUser(*pairs)
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
     * -- ONLY FOR TESTING --
     * Updates user data only in local storage.
     * @param user The user to be updated
     */
    fun updateUserDetails(user: User?) {
        if (user == null) {
            this.user = null
            storage.removeFile(Storage.FILES.USERINFO)
            return
        }
        this.user = user
        storage.writeBackUser(user)
    }

    /**
     * Returns the user picture of the current user from either cache or local storage.
     * @return The user picture gotten from storage/cache.
     */
    fun getUserPicture(): Bitmap? {
        if (userPicture != null)
            return userPicture
        return storage.getUserPicture()
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
        userPicture = fbRepo.getUserProfilePictureImage(user!!.uid)
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
        fbRepo.setUserProfilePicture(bitmap)
        storage.writeBackUserPicture(bitmap)
    }

    /**
     * Retrieves the stats data from cache or local storage.
     * @param position the position of the stats.
     * @return the stats data for the user.
     */
    fun getStatsData(position: Int): List<UserStat> {
        if (::userStatData.isInitialized) return userStatData
        userStatData = storage.getStatsData() ?: listOf()
        return userStatData
    }

    /**
     * Retrieves the stats data from firebase.
     * @param position the position of the stats.
     * @return the stats data for the user.
     */
    suspend fun getStatsDataAsync(position: Int): List<UserStat> {
        if (!isInternetAvailable()) {
            Log.d("CACHE", "INTERNET NOT AVAILABLE")
            return getStatsData(position)
        }
        userStatData = FirebaseHelper.getStatsData(position)
        Log.d("Cache", userStatData.size.toString())
        storage.writeBackStatsData(userStatData)
        return userStatData
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
     * @param position the position of the leaderboard to load.
     * @return A list of LeaderBoardInfo fetched from local storage or cache.
     */
    fun getLeaderBoardData(position: Int): List<LeaderBoardInfo> {
        if (::leaderBoardData.isInitialized) return leaderBoardData
        leaderBoardData = storage.getLeaderBoardData() ?: listOf()
        return leaderBoardData
    }

    /**
     * Gets leaderboard data firebase.
     * @param position the position of the leaderboard to load.
     * @return A list of LeaderBoardInfo fetched from firebase.
     */
    suspend fun getLeaderBoardDataAsync(position: Int): List<LeaderBoardInfo> {
        if (!isInternetAvailable()) {
            Log.d("CACHE", "INTERNET NOT AVAILABLE")
            return getLeaderBoardData(position)
        }
        leaderBoardData = FirebaseHelper.getLeaderBoard(position)
        Log.d("Cache", leaderBoardData.size.toString())
        storage.writeBackLeaderBoardData(leaderBoardData)
        return leaderBoardData
    }

    private fun isInternetAvailable(): Boolean {
        return try {
            val ipAddr: InetAddress = InetAddress.getByName("www.google.com")
            //You can replace it with your name
            !ipAddr.equals("")
        } catch (e: Exception) {
            Log.d("Cache", e.toString())
            false
        }
    }

    companion object {
        var cache: Cache? = null
        fun getInstance(): Cache? {
            return cache
        }

        fun createInstance(ctx: Context): Cache {
            cache = Cache(ctx.applicationContext, true)
            return cache!!
        }

        fun createInstance(ctx: Context, repository: FirebaseRepository): Cache {
            cache = Cache(ctx.applicationContext)
            cache!!.fbRepo = repository
            return cache!!
        }
    }
}