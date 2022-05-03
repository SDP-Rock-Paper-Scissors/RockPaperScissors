package ch.epfl.sweng.rps.db

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import ch.epfl.sweng.rps.models.*
import ch.epfl.sweng.rps.models.Game.Companion.toGame
import ch.epfl.sweng.rps.utils.toListOf
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
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
        firebase.usersFriendRequestOfUid(uid)
            .add(FriendRequest(from = getCurrentUid())).await()
    }

    override suspend fun listFriendRequests(): List<FriendRequest> {
        return firebase.usersFriendRequestOfUid(getCurrentUid()).get().await().documents.toListOf()
    }

    override suspend fun getFriends(): List<String> {
        return firebase.usersFriendRequestOfUid(getCurrentUid())
            .whereEqualTo("accepted", true)
            .get().await().documents
            .map { it.toObject<FriendRequest>()!!.from }
    }

    override suspend fun acceptFriendRequestFrom(userUid: String) {
        firebase.usersFriendRequestOfUid(userUid)
            .whereEqualTo("from", getCurrentUid()).limit(1)
            .get().await().documents.first().reference.update("accepted", true).await()
    }

    override suspend fun getGame(gameId: String): Game? {
        val doc: DocumentSnapshot = firebase.gamesCollection.document(gameId).get().await()
        return doc.toGame()
    }


    override suspend fun getLeaderBoardScore(): List<TotalScore> {
        return firebase.scoresCollection.orderBy("score", Query.Direction.DESCENDING).get()
            .await().documents.map{
                it.toObject<TotalScore>()!!
            }


    }

    override suspend fun gamesOfUser(uid: String): List<Game> {
        return firebase.gamesCollection.whereArrayContains(Game.FIELDS.PLAYERS, uid).get()
            .await().documents.map { it.toGame()!! }
    }

    override suspend fun myActiveGames(): List<Game> {
        return firebase.gamesCollection
            .whereArrayContains(Game.FIELDS.PLAYERS, getCurrentUid())
            .whereEqualTo(Game.FIELDS.DONE, false)
            .get().await().documents.map { it.toGame()!! }
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
}


