package ch.epfl.sweng.rps.persistance

import ch.epfl.sweng.rps.models.User
import ch.epfl.sweng.rps.models.UserStat
import java.io.File

interface Storage {
    fun getFile(file : FILES) : File
    fun removeFile(file : FILES) : Boolean
    fun getUserDetails() : User?
    fun getStatsData() : List<UserStat>?
    fun writeBackUser(user: User)
    fun writeBackStatsData(data : List<UserStat>)
    enum class FILES(val file:String){
        USERINFO("USER"),
        STATSDATA("STATSDATA")
    }
}