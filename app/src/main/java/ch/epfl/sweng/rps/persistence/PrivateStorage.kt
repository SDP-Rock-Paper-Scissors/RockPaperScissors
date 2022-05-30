package ch.epfl.sweng.rps.persistence

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import ch.epfl.sweng.rps.models.*
import com.google.gson.Gson
import android.net.Uri
import com.google.gson.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.lang.reflect.Type
import java.util.*

class PrivateStorage constructor(val context: Context) : Storage {
    private val gson = GsonBuilder()
        .registerTypeAdapter(Uri::class.java, UriDeserializer())
        .registerTypeAdapter(Uri::class.java, UriSerializer())
        .create()

    override fun getFile(file: Storage.FILES): File {
        return File(context.filesDir, file.file)
    }

    override fun deleteFile(file: Storage.FILES): Boolean {
        return getFile(file).delete()
    }

    override fun getUser(): User? {
        val userFile = getFile(Storage.FILES.USERINFO)
        if (!userFile.exists())
            return null
        val json = userFile.readText()
        return gson.fromJson(json, User::class.java)
    }

    override fun writeBackUser(user: User) {
        val f = getFile(Storage.FILES.USERINFO)
        f.writeText(gson.toJson(user))
    }

    override fun getStatsData(): List<UserStat>? {
        val statsFile = getFile(Storage.FILES.STATSDATA)
        if (!statsFile.exists())
            return null
        val json = statsFile.readText()
        val arr = gson.fromJson(json, Array<UserStat>::class.java)
        return arr.toList()
    }

    override fun removeFile(file: Storage.FILES): Boolean {
        return false
    }

    override fun getUserDetails(): User? {
        return null
    }

    override fun getFriends(): List<FriendsInfo>? {
        val statsFile = getFile(Storage.FILES.FRIENDS)
        if (!statsFile.exists())
            return null
        val json = statsFile.readText()
        val arr = Gson().fromJson(json, Array<FriendsInfo>::class.java)
        return arr.toList()
    }

    override fun getFriendReqs(): List<FriendRequestInfo>? {
        val statsFile = getFile(Storage.FILES.REQUESTS)
        if (!statsFile.exists())
            return null
        val json = statsFile.readText()
        val arr = Gson().fromJson(json, Array<FriendRequestInfo>::class.java)
        return arr.toList()
    }

    override fun writeBackFriends(data : List<FriendsInfo>){
        val gson = Gson()
        val json = gson.toJson(data.toTypedArray(), Array<UserStat>::class.java)
        val f = getFile(Storage.FILES.FRIENDS)
        f.writeText(json)
    }

    override fun writeBackFriendReqs(data : List<FriendRequestInfo>){
        val gson = Gson()
        val json = gson.toJson(data.toTypedArray(), Array<UserStat>::class.java)
        val f = getFile(Storage.FILES.REQUESTS)
        f.writeText(json)
    }


    override fun getLeaderBoardData(): List<LeaderBoardInfo>? {
        val leaderBoardFile = getFile(Storage.FILES.LEADERBOARDDATA)
        if (!leaderBoardFile.exists())
            return null
        val json = leaderBoardFile.readText()
        val arr = gson.fromJson(json, Array<LeaderBoardInfo>::class.java)
        return arr.toList()
    }

    override fun writeBackStatsData(data: List<UserStat>) {
        val json = gson.toJson(data.toTypedArray(), Array<UserStat>::class.java)
        val f = getFile(Storage.FILES.STATSDATA)
        f.writeText(json)
    }

    override fun writeBackLeaderBoardData(data: List<LeaderBoardInfo>) {
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

    internal class UriDeserializer : JsonDeserializer<Uri> {
        override fun deserialize(
            json: JsonElement,
            typeOfT: Type?,
            context: JsonDeserializationContext?
        ): Uri {
            val uri = json.asString
            return Uri.parse(uri)
        }
    }

    internal class UriSerializer : JsonSerializer<Uri> {
        override fun serialize(
            src: Uri?,
            typeOfSrc: Type?,
            context: JsonSerializationContext?
        ): JsonElement {
            if (src == null)
                return JsonNull.INSTANCE
            return JsonPrimitive(src.toString())
        }
    }
}