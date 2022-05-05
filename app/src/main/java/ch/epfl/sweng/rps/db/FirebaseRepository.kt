package ch.epfl.sweng.rps.db

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import ch.epfl.sweng.rps.models.*
import ch.epfl.sweng.rps.models.FriendRequest
import ch.epfl.sweng.rps.utils.toListOf
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.net.URI


class FirebaseRepository private constructor(
    private val firebase: FirebaseReferences
) : Repository {

    companion object {
        internal fun createInstance(firebaseReferences: FirebaseReferences): FirebaseRepository {
            return FirebaseRepository(firebaseReferences)
        }
    }

    override suspend fun updateUser(vararg pairs: Pair<User.Field, Any>) {
        val arguments = FirebaseHelper.processUserArguments(*pairs)
        val uid = getCurrentUid()
        firebase.usersCollection.document(uid).update(arguments).await()
    }



    override suspend fun getUser(uid: String): User? {
        val user = firebase.usersCollection.document(uid).get().await()
        return user?.toObject<User>()
    }

    override suspend fun getUserProfilePictureUrl(uid:String): URI? {
        return if (getUser(uid)!!.has_profile_photo)
            firebase.profilePicturesFolder.child(uid).downloadUrl.await().toURI()
        else
            null
    }

    override suspend fun getUserProfilePictureImage(uid:String): Bitmap? {
        return if (getUser(uid)!!.has_profile_photo){
             val uri = firebase.profilePicturesFolder.child(uid).downloadUrl.await().toURI()
             Log.d("URI" , uri.path!!)
             BitmapFactory.decodeStream(java.net.URL(uri.toURL(),"" ).openStream())
        }
        else
            null
    }
    override suspend fun setUserProfilePicture(image : Bitmap){
        val baos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        updateUser(Pair(User.Field.HAS_PROFILE_PHOTO , true))
        firebase.profilePicturesFolder.child(getCurrentUid()).putBytes(data)
    }


    override suspend fun createThisUser(name: String?, email: String?): User {
        val uid = getCurrentUid()
        val user = FirebaseHelper.userFrom(uid, name, email)
        firebase.usersCollection.document(uid).set(user).await()
        return user
    }

    override fun rawCurrentUid(): String? = FirebaseAuth.getInstance().currentUser?.uid

    override suspend fun sendFriendRequestTo(uid: String) {
        firebase.usersFriendRequest
            .add(FriendRequest.build(from = getCurrentUid(), to = uid, timestamp = Timestamp.now()))
            .await()
    }

    override suspend fun listFriendRequests(): List<FriendRequest> {
        return firebase.usersFriendRequest
            .whereArrayContains("users", getCurrentUid())
            .whereNotEqualTo("status", FriendRequest.Status.PENDING).get()
            .await().toObjects(FriendRequest::class.java)
    }

    override suspend fun getFriends(): List<String> {
        val me = getCurrentUid()
        return firebase.usersFriendRequest
            .whereEqualTo("status", FriendRequest.Status.ACCEPTED)
            .whereArrayContains("members", getCurrentUid())
            .get().await().documents
            .map { it.toObject<FriendRequest>()!!.users / me }
    }


    override suspend fun changeFriendRequestToStatus(
        userUid: String,
        status: FriendRequest.Status
    ) {
        firebase.usersFriendRequest
            .whereArrayContains("users", userUid)
            .whereArrayContains("users", getCurrentUid())
            .whereNotEqualTo("status", FriendRequest.Status.PENDING)
            .limit(1)
            .get().await().documents.first().reference
            .update("status", status).await()
    }

    override suspend fun getGame(gameId: String): Game? {
        return firebase.gamesCollection.document(gameId).get().await().toObject<Game>()
    }


    override suspend fun getLeaderBoardScore(): List<TotalScore> {
        return firebase.scoresCollection.orderBy("score", Query.Direction.DESCENDING).get()
            .await().documents.map {
                it.toObject<TotalScore>()!!
            }
    }

    override suspend fun gamesOfUser(uid: String): List<Game> {
        return firebase.gamesCollection.whereArrayContains("players", uid).get()
            .await().documents.toListOf()
    }

    override suspend fun myActiveGames(): List<Game> {
        return firebase.gamesCollection
            .whereArrayContains("players", getCurrentUid())
            .whereEqualTo("done", false)
            .get().await().documents.toListOf()
    }

    override suspend fun statsOfUser(uid: String): UserStats {
        return firebase.usersCollection.document(uid).collection("stats").document("games").get()
            .await().toObject<UserStats>()!!
    }

    override suspend fun listInvitations(): List<Invitation> {
        return firebase.invitationsOfUid(getCurrentUid()).get()
            .await().documents.toListOf()
    }


    private fun Uri.toURI(): URI = URI(toString())

    private operator fun <T> List<T>.div(el: T): T = first { it != el }
}


