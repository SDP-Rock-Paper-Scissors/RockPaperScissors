package ch.epfl.sweng.rps.persistance

import android.content.Context
import ch.epfl.sweng.rps.models.User
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
        if (user != null) return user
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

    fun writeBackUser(user: User) {
        val data = Properties()
        if (user.username != null) data["username"] = user.username
        data["uid"] = user.uid
        if (user.email != null) data["email"] = user.email
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