package ch.epfl.sweng.rps.db.interfaces

import ch.epfl.sweng.rps.db.interfaces.Database
import com.google.android.gms.tasks.Task

interface SocialDatabase {
    fun addFriend(uid: String): Task<Unit>

    val db: Database

    fun getFriends() = getFriends(db.getCurrentUid()!!)

    fun getFriends(uid: String): Task<List<String>>

}