package ch.epfl.sweng.rps.persistance

import android.content.Context
import ch.epfl.sweng.rps.models.User
import ch.epfl.sweng.rps.models.UserStat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.util.*

class PrivateStorage constructor(val context: Context) : Storage {
    val user: User? = null
    val matches = null
    override fun getFile(file: Storage.FILES): File {
        return File(context.filesDir, file.file)
    }

    override fun removeFile(file: Storage.FILES): Boolean {
        return File(context.filesDir, file.file).delete()
    }

    override fun getUserDetails(): User? {
        val userFile = getFile(Storage.FILES.USERINFO)
        if (!userFile.exists())
            return null
        val values = HashMap<String, String>()
        val properties = Properties()
        properties.load(userFile.bufferedReader())
        for (key in properties.stringPropertyNames()) {
            values[key] = properties[key].toString()
        }
        return User(
            values["username"].toString(), values["uid"].toString(),
            "", false, values["email"]
        )
    }

    override fun writeBackUser(user: User) {
        val data = Properties()
        if (user.username != null) data["username"] = user.username
        data["uid"] = user.uid
        if (user.email != null) data["email"] = user.email
        val f = getFile(Storage.FILES.USERINFO)
        data.store(f.bufferedWriter(), null)
    }

    override fun getStatsData(): List<UserStat>? {
        val statsFile = getFile(Storage.FILES.STATSDATA)
        if (!statsFile.exists())
            return null
        val json = statsFile.readText()
        val arr = Gson().fromJson(json, Array<UserStat>::class.java)
        return arr.toList()
    }
    override fun writeBackStatsData(data : List<UserStat>){
        val gson = Gson()
        val json = gson.toJson(data.toTypedArray(), Array<UserStat>::class.java)
        val f = getFile(Storage.FILES.STATSDATA)
        f.writeText(json)
    }
}