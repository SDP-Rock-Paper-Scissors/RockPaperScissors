package ch.epfl.sweng.rps.persistence

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import ch.epfl.sweng.rps.db.FirebaseHelper
import ch.epfl.sweng.rps.db.FirebaseRepository
import ch.epfl.sweng.rps.models.*
import ch.epfl.sweng.rps.services.ServiceLocator
import java.net.InetAddress


class Cache private constructor(private val ctx:Context, val preferFresh:Boolean = false) {
    companion object {
        var cache:Cache? = null
        fun getInstance(): Cache? {
            return cache
        }
        fun createInstance(ctx : Context ): Cache {
            cache = Cache(ctx.applicationContext, true)
            return cache!!
        }
        fun createInstance(ctx:Context, repository: FirebaseRepository): Cache{
            cache = Cache(ctx.applicationContext)
            cache!!.fbRepo = repository
            return cache!!
        }
    }
    private var fbRepo = ServiceLocator.getInstance().repository
    private val storage:Storage = PrivateStorage(ctx)
    private var user:User? = null
    private var userPicture : Bitmap? = null
    private lateinit var userStatData : List<UserStat>
    private lateinit var leaderBoardData: List<LeaderBoardInfo>
    private lateinit var myFriends: List<FriendsInfo>
    private lateinit var myFriendReqs: List<FriendRequestInfo>

    fun getUserDetails() : User? {
        if(user != null) return user
        user = storage.getUserDetails()
        return user
    }
    suspend fun getUserDetailsAsync(callback: (User?) -> Unit) {
        if(!isInternetAvailable())
            return
        val uid = fbRepo.getCurrentUid()
        user = fbRepo.getUser(uid)
        callback(user)
    }
    suspend fun updateUserDetails(user:User, vararg pairs:Pair<User.Field, Any>) {
        this.user = user
        storage.writeBackUser(user)
        fbRepo.updateUser(*pairs)
    }
    fun updateStatsData(statsData:List<UserStat>){
        userStatData = statsData
        storage.writeBackStatsData(statsData)
    }

    fun updateUserDetails(user:User?) {
        if(user == null){
            this.user = null
            storage.removeFile(Storage.FILES.USERINFO)
            return
        }
        this.user = user
        storage.writeBackUser(user)
    }
    fun getUserPicture() : Bitmap?{
        if(userPicture != null)
            return userPicture
        return storage.getUserPicture()
    }
    suspend fun getUserPictureAsync() : Bitmap?{
        if(!isInternetAvailable()){
            return getUserPicture()
        }
        if(user == null)
            return null
        userPicture = fbRepo.getUserProfilePictureImage(user!!.uid)
        Log.d("UserPic" , userPicture.toString())
        userPicture?.let { storage.writeBackUserPicture(it)}
        return userPicture
    }
    suspend fun updateUserPicture(bitmap: Bitmap){
        userPicture = bitmap
        fbRepo.setUserProfilePicture(bitmap)
        storage.writeBackUserPicture(bitmap)
    }

    fun getStatsData(position: Int):List<UserStat> {
       if(::userStatData.isInitialized) return userStatData
       userStatData = storage.getStatsData() ?: listOf()
       return userStatData
    }
    suspend fun getStatsDataAsync(position:Int):List<UserStat>{
        if(!isInternetAvailable()) {
            Log.d("CACHE", "INTERNET NOT AVAILABLE")
            return getStatsData(position)
        }
        userStatData = FirebaseHelper.getStatsData(position)
        Log.d("Cache", userStatData.size.toString())
        storage.writeBackStatsData(userStatData)
        return userStatData
    }


    fun updateLeaderBoardData(lBData:List<LeaderBoardInfo>){
        leaderBoardData = lBData
        storage.writeBackLeaderBoardData(lBData)
    }

    fun getLeaderBoardData(position: Int):List<LeaderBoardInfo> {
        if(::leaderBoardData.isInitialized) return leaderBoardData
        leaderBoardData = storage.getLeaderBoardData() ?: listOf()
        return leaderBoardData
    }

    suspend fun getLeaderBoardDataAsync(position: Int):List<LeaderBoardInfo>{
        if(!isInternetAvailable()) {
            Log.d("CACHE", "INTERNET NOT AVAILABLE")
            return getLeaderBoardData(position)
        }
        leaderBoardData = FirebaseHelper.getLeaderBoard(position)
        Log.d("Cache", leaderBoardData.size.toString())
        storage.writeBackLeaderBoardData(leaderBoardData)
        return leaderBoardData
    }

    fun getFriends():List<FriendsInfo> {
        if(::myFriends.isInitialized) return myFriends
        myFriends = storage.getFriends() ?: listOf()
        return myFriends
    }

    suspend fun getFriendsAsync():List<FriendsInfo>{
        if(!isInternetAvailable()) {
            Log.d("CACHE", "INTERNET NOT AVAILABLE")
            return getFriends()
        }
        myFriends = FirebaseHelper.getFriends()
        Log.d("Cache", myFriends.size.toString())
        storage.writeBackFriends(myFriends)
        return myFriends
    }

    fun getFriendReqs():List<FriendRequestInfo> {
        if(::myFriendReqs.isInitialized) return myFriendReqs
        myFriendReqs = storage.getFriendReqs() ?: listOf()
        return myFriendReqs
    }

    suspend fun getFriendReqsAsync():List<FriendRequestInfo>{
        if(!isInternetAvailable()) {
            Log.d("CACHE", "INTERNET NOT AVAILABLE")
            return getFriendReqs()
        }
        myFriendReqs = FirebaseHelper.getFriendReqs()
        Log.d("Cache", myFriends.size.toString())
        storage.writeBackFriendReqs(myFriendReqs)
        return myFriendReqs
    }





    fun isInternetAvailable(): Boolean {
        return try {
            val ipAddr: InetAddress = InetAddress.getByName("www.google.com")
            //You can replace it with your name
            !ipAddr.equals("")
        } catch (e: Exception) {
            Log.d("Cache", e.toString())
            false
        }
    }
}