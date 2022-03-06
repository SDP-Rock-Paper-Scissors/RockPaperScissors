package ch.epfl.sweng.rps.db

import ch.epfl.sweng.rps.db.interfaces.SocialDatabase
import ch.epfl.sweng.rps.models.FriendRequest
import com.google.android.gms.tasks.Task

class SocialDatabaseImpl(override val db: FirestoreDatabase) : SocialDatabase {
    override fun addFriend(uid: String) =
        usersFriendRequest(uid)
            .add(FriendRequest(from = db.getCurrentUid()!!))
            .continueWith { }

    override fun getFriends(uid: String): Task<List<String>> {
        return db.getUser(uid).continueWith { t -> t.result!!.friends }
    }

    private fun usersFriendRequest(uid: String) =
        db.usersCollection.document(uid).collection("friend_requests")
}