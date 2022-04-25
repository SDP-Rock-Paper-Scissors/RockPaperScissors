package ch.epfl.sweng.rps.persistance

import android.content.Context
import ch.epfl.sweng.rps.db.FirebaseHelper
import ch.epfl.sweng.rps.db.FirebaseReferences
import ch.epfl.sweng.rps.db.FirebaseRepository
import ch.epfl.sweng.rps.models.User
import ch.epfl.sweng.rps.models.UserStat
import java.net.InetAddress


class Cache private constructor(val ctx:Context, val preferFresh:Boolean = false) {
    companion object {
        var cache:Cache? = null
        fun getInstance(ctx:Context, preferFresh: Boolean = false): Cache {
            if(cache != null) return cache!!
            cache = Cache(ctx, preferFresh)
            return cache!!
        }
    }
    private val fbRef = FirebaseReferences()
    private val fbRepo = FirebaseRepository.createInstance(fbRef)
    private val storage:Storage = PrivateStorage(ctx)
    private var user:User? = null
    private lateinit var userStatData : List<UserStat>
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
    fun getStatsData(position: Int):List<UserStat> {
       if(::userStatData.isInitialized) return userStatData
       userStatData = storage.getStatsData() ?: listOf()
       return userStatData
    }
    suspend fun getStatsDataAsync(position:Int):List<UserStat>{
        if(!isInternetAvailable())
            return getStatsData(position)
        userStatData = FirebaseHelper.getStatsData(position)
        storage.writeBackStatsData(userStatData)
        return userStatData
    }
    fun isInternetAvailable(): Boolean {
        return try {
            val ipAddr: InetAddress = InetAddress.getByName("google.com")
            //You can replace it with your name
            !ipAddr.equals("")
        } catch (e: Exception) {
            false
        }
    }
}