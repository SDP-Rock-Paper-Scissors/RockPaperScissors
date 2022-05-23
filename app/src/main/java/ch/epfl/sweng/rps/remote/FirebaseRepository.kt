package ch.epfl.sweng.rps.remote

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import ch.epfl.sweng.rps.models.remote.User
import ch.epfl.sweng.rps.remote.friends.FirebaseFriendsRepository
import ch.epfl.sweng.rps.remote.friends.FriendsRepository
import ch.epfl.sweng.rps.remote.games.FirebaseGamesRepository
import ch.epfl.sweng.rps.remote.games.GamesRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.net.URI
import java.net.URL


class FirebaseRepository private constructor(
    val firebase: FirebaseReferences
) : Repository {


    override val friends: FriendsRepository by lazy {
        FirebaseFriendsRepository(this)
    }
    override val games: GamesRepository by lazy {
        FirebaseGamesRepository(this)
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

    override suspend fun getUserProfilePictureUrl(uid: String): URI? {
        return if (getUser(uid)!!.has_profile_photo)
            firebase.profilePicturesFolder.child(uid).downloadUrl.await().toURI()
        else
            null
    }

    override suspend fun getUserProfilePictureImage(uid: String): Bitmap? {
        return if (getUser(uid)!!.has_profile_photo) {
            val uri = firebase.profilePicturesFolder.child(uid).downloadUrl.await()
            Log.d("URI", uri.path!!)
            @Suppress("BlockingMethodInNonBlockingContext")
            withContext(Dispatchers.IO) {
                val inputStream: InputStream = URL(uri.toString()).openStream()
                BitmapFactory.decodeStream(inputStream)
            }

        } else
            null
    }

    override suspend fun setUserProfilePicture(image: Bitmap, waitForUploadTask: Boolean) {
        val baos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        updateUser(Pair(User.Field.HAS_PROFILE_PHOTO, true))
        val task = firebase.profilePicturesFolder.child(getCurrentUid()).putBytes(data)
        if (waitForUploadTask)
            task.await()
    }

    override suspend fun createThisUser(name: String?, email: String?): User {
        val uid = getCurrentUid()
        val user = FirebaseHelper.userFrom(uid, name, email)
        firebase.usersCollection.document(uid).set(user).await()
        return user
    }

    override fun rawCurrentUid(): String? = FirebaseAuth.getInstance().currentUser?.uid
    private fun Uri.toURI(): URI = URI(toString())
    private operator fun <T> List<T>.div(el: T): T = first { it != el }

    companion object {
        internal fun createInstance(firebaseReferences: FirebaseReferences): FirebaseRepository {
            return FirebaseRepository(firebaseReferences)
        }
    }
}


