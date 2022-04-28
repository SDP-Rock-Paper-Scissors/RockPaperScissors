package ch.epfl.sweng.rps.persistence

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import ch.epfl.sweng.rps.db.Env
import ch.epfl.sweng.rps.db.FirebaseHelper
import ch.epfl.sweng.rps.db.FirebaseReferences
import ch.epfl.sweng.rps.db.FirebaseRepository
import ch.epfl.sweng.rps.models.LeaderBoardInfo
import ch.epfl.sweng.rps.models.User
import ch.epfl.sweng.rps.models.UserStat
import ch.epfl.sweng.rps.services.ServiceLocator
import java.net.InetAddress


class Cache private constructor(private val ctx:Context, val preferFresh:Boolean = false) {
    companion object {
        var cache:Cache? = null
        fun getInstance(preferFresh: Boolean = false): Cache? {
            return cache
        }
        fun createInstance(ctx : Context ): Cache {
            cache = Cache(ctx.applicationContext, true)
            return cache!!
        }
    }
    private val fbRepo = ServiceLocator.getInstance().repository
    private val storage:Storage = PrivateStorage(ctx)
    private var user:User? = null
    private var userPicture : Bitmap? = null
    private lateinit var userStatData : List<UserStat>
    private lateinit var leaderBoardData: List<LeaderBoardInfo>
    fun getUserDetails() : User? {
        if(user != null) return user
        user = storage.getUserDetails()
        return user
    }
    suspend fun getUserDetailsAsync(callback: (User?) -> Unit) {
        if(user != null) {
            callback(user!!)
            return
        }
        if(!isInternetAvailable())
            return
        val uid = fbRepo.getCurrentUid()
        user = fbRepo.getUser(uid)
        callback(user)
    }
    suspend fun updateUserDetails(user:User, vararg pairs:Pair<User.Field, Any>) {
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
        storage.writeBackUser(user)
    }
    fun getUserPicture() : Bitmap?{
        return storage.getUserPicture()
    }
    suspend fun getUserPictureAsync(uid:String) : Bitmap?{
        if(!isInternetAvailable()){
            return getUserPicture()
        }
        userPicture = fbRepo.getUserProfilePictureImage(uid) ?: null
        Log.d("UserPic" , userPicture.toString())
        userPicture?.let { storage.writeBackUserPicture(it)}
        return userPicture
    }
    suspend fun updateUserPicture(uid:String, bitmap: Bitmap){
        fbRepo.setUserProfilePicture(uid, bitmap)
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

    fun getLeaderBoardData():List<LeaderBoardInfo> {
        if(::leaderBoardData.isInitialized) return leaderBoardData
        leaderBoardData = storage.getLeaderBoardData() ?: listOf()
        return leaderBoardData
    }
    suspend fun getLeaderBoardDataAsync():List<LeaderBoardInfo>{
        if(!isInternetAvailable()) {
            Log.d("CACHE", "INTERNET NOT AVAILABLE")
            return getLeaderBoardData()
        }
        leaderBoardData = FirebaseHelper.getLeaderBoard()
        Log.d("Cache", leaderBoardData.size.toString())
        storage.writeBackLeaderBoardData(leaderBoardData)
        return leaderBoardData
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