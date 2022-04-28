package ch.epfl.sweng.rps.persistence

import android.content.Context
import ch.epfl.sweng.rps.models.LeaderBoardInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import ch.epfl.sweng.rps.models.User
import ch.epfl.sweng.rps.models.UserStat
import com.google.gson.Gson
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
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

    override fun getLeaderBoardData(): List<LeaderBoardInfo>? {
        val leaderBoardFile = getFile(Storage.FILES.LEADERBOARDDATA)
        if (!leaderBoardFile.exists())
            return null
        val json = leaderBoardFile.readText()
        val arr = Gson().fromJson(json, Array<LeaderBoardInfo>::class.java)
        return arr.toList()
    }

    override fun writeBackStatsData(data : List<UserStat>){
        val gson = Gson()
        val json = gson.toJson(data.toTypedArray(), Array<UserStat>::class.java)
        val f = getFile(Storage.FILES.STATSDATA)
        f.writeText(json)
    }

    override fun writeBackLeaderBoardData(data : List<LeaderBoardInfo>){
        val gson = Gson()
        val json = gson.toJson(data.toTypedArray(), Array<LeaderBoardInfo>::class.java)
        val f = getFile(Storage.FILES.LEADERBOARDDATA)
        f.writeText(json)
    }

    override fun writeBackUserPicture(bitmap: Bitmap) {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        FileOutputStream(getFile(Storage.FILES.USERPICTURE)).write(data)
    }

    override fun getUserPicture(): Bitmap? {
        val userFile = getFile(Storage.FILES.USERPICTURE)
        if (!userFile.exists())
            return null
        return BitmapFactory.decodeFile(getFile(Storage.FILES.USERPICTURE).path)
    }
}