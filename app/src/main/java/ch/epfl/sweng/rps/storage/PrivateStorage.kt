package ch.epfl.sweng.rps.storage

import android.content.Context
import ch.epfl.sweng.rps.models.User
import java.io.File
import java.util.*

class PrivateStorage constructor(context: Context) : Storage {
    val context = context
    val user: User? = null
    val matches = null
    override fun getFile(file: Storage.FILES): File {
        return File(context.filesDir, file.file)
    }

    override fun removeFile(file: Storage.FILES): Boolean {
        return File(context.filesDir, file.file).delete()
    }

    override fun getUserDetails(): User? {
        if (user != null) return user
        val userFile = getFile(Storage.FILES.USERINFO)
        if(!userFile.exists())
            return null
        val values = HashMap<String, String>()
        val properties = Properties()
        properties.load(userFile.bufferedReader())
        for (key in properties.stringPropertyNames()) {
            values[key] = properties.get(key).toString()
        }
        return User(
            values["username"].toString(), values["uid"].toString(),
            "", false, values["email"]
        )
    }
    fun writeBackUser(user:User){
        val data: Properties = Properties()
        data.put("username", user.username)
        data.put("uid", user.uid)
        data.put("email",user.email)
        val f = getFile(Storage.FILES.USERINFO)
        data.store(f.bufferedWriter(), null)
    }
    override fun getUserSettings() {
        TODO("Not yet implemented")
    }

    override fun getMatchesDetails() {
        TODO("Not yet implemented")
    }
}