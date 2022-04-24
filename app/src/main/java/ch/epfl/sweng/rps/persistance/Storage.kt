package ch.epfl.sweng.rps.persistance

import ch.epfl.sweng.rps.models.User
import java.io.File

interface Storage {
    fun getFile(file : FILES) : File
    fun removeFile(file : FILES) : Boolean
    fun getUserDetails() : User?
    fun getUserSettings()
    fun getMatchesDetails()
    enum class FILES(val file:String){
        USERINFO("USER"),
        MATCHESINFO("MATCHES")
    }
}