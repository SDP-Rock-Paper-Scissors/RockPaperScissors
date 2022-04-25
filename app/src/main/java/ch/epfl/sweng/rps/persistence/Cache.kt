package ch.epfl.sweng.rps.persistence

import android.content.Context
import android.util.Log
import ch.epfl.sweng.rps.db.Env
import ch.epfl.sweng.rps.db.FirebaseHelper
import ch.epfl.sweng.rps.db.FirebaseReferences
import ch.epfl.sweng.rps.db.FirebaseRepository
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

    fun updateUserDetails(user:User) {
        storage.writeBackUser(user)
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