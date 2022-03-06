package ch.epfl.sweng.rps.db.interfaces

import android.net.Uri
import ch.epfl.sweng.rps.models.User
import com.google.android.gms.tasks.Task

interface Database {
    fun updateUser(vararg pairs: Pair<User.Field, Any>): Task<Void>

    fun getCurrentUid(): String?

    fun getUser(uid: String): Task<User>

    fun getUserProfilePicture(uid: String): Task<Uri?>

    fun createUser(name: String, email: String?): Task<Void>
}