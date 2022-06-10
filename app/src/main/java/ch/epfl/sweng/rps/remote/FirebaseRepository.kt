package ch.epfl.sweng.rps.remote

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.annotation.VisibleForTesting
import ch.epfl.sweng.rps.models.remote.User
import ch.epfl.sweng.rps.remote.friends.FirebaseFriendsRepository
import ch.epfl.sweng.rps.remote.friends.FriendsRepository
import ch.epfl.sweng.rps.remote.games.FirebaseGamesRepository
import ch.epfl.sweng.rps.remote.games.GamesRepository
import ch.epfl.sweng.rps.utils.SuspendResult
import ch.epfl.sweng.rps.utils.guardSuspendable
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.net.URI
import java.net.URL


/**
 * This class is the implementation of the [Repository] interface.
 * It is used to communicate with the Firebase database.
 */
class FirebaseRepository private constructor(
    internal val firebase: FirebaseReferences,
    internal val auth: FirebaseAuth,
) : Repository {


    override val friends: FriendsRepository by lazy {
        FirebaseFriendsRepository(this)
    }
    override val games: GamesRepository by lazy {
        FirebaseGamesRepository(this)
    }

    override suspend fun updateUser(vararg pairs: Pair<User.Field, Any>): SuspendResult<Unit> =
        guardSuspendable {
            val arguments = FirebaseHelper.processUserArguments(*pairs)
            val uid = getCurrentUid()
            firebase.usersCollection.document(uid).update(arguments).await()
        }

    override suspend fun getUser(uid: String): SuspendResult<User?> = guardSuspendable {
        firebase.usersCollection.document(uid).get().await()?.toObject<User>()
    }

    override suspend fun getUserProfilePictureUrl(uid: String): SuspendResult<URI?> =
        getUser(uid).then {
            if (it != null && it.has_profile_photo)
                firebase.profilePicturesFolder.child(uid).downloadUrl.await().toURI()
            else
                null
        }


    override suspend fun getUserProfilePictureImage(uid: String): Bitmap? {
        return if (getUser(uid).asData?.value?.has_profile_photo == true) {
            val uri = firebase.profilePicturesFolder.child(uid).downloadUrl.await()
            @Suppress("BlockingMethodInNonBlockingContext")
            withContext(Dispatchers.IO) {
                val inputStream: InputStream = URL(uri.toString()).openStream()
                BitmapFactory.decodeStream(inputStream)
            }
        } else
            null
    }

    override suspend fun setUserProfilePicture(
        image: Bitmap,
        waitForUploadTask: Boolean
    ): SuspendResult<Unit> =
        guardSuspendable {
            val stream = ByteArrayOutputStream()
            image.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            val data = stream.toByteArray()
            updateUser(Pair(User.Field.HAS_PROFILE_PHOTO, true)).getOrThrow()
            val task = firebase.profilePicturesFolder.child(getCurrentUid()).putBytes(data)
            if (waitForUploadTask)
                task.await()
        }

    override suspend fun createThisUser(name: String?, email: String?) = guardSuspendable {
        val uid = getCurrentUid()
        val user = FirebaseHelper.userFrom(uid, name, email)
        firebase.usersCollection.document(uid).set(user).await()
        user
    }

    override fun rawCurrentUid(): String? = auth.currentUser?.uid
    private fun Uri.toURI(): URI = URI(toString())
    private operator fun <T> List<T>.div(el: T): T = first { it != el }

    companion object {
        @VisibleForTesting
        internal fun createInstance(
            firebaseReferences: FirebaseReferences,
            auth: FirebaseAuth = FirebaseAuth.getInstance()
        ): FirebaseRepository =
            FirebaseRepository(firebaseReferences, auth)
    }
}


