package ch.epfl.sweng.rps.persistance

import android.content.Context
import ch.epfl.sweng.rps.db.FirebaseReferences
import ch.epfl.sweng.rps.db.FirebaseRepository
import ch.epfl.sweng.rps.models.User

class Cache private constructor(val ctx:Context, val preferFresh:Boolean = false) {
    companion object {
        var cache:Cache? = null
        fun getInstance(ctx:Context, preferFresh: Boolean = false): Cache {
            if(cache != null) return cache!!
            cache = Cache(ctx, preferFresh)
            return cache!!
        }
    }
    val fbRef = FirebaseReferences()
    val fbRepo = FirebaseRepository.createInstance(fbRef)
    val storage:Storage = PrivateStorage(ctx)
    var user:User? = null
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
        val uid = fbRepo.getCurrentUid()
        user = fbRepo.getUser(uid)
        callback(user)
    }
    fun getUserSettings(){

    }
    fun getMatchesDetails() {

    }
}