package ch.epfl.sweng.rps.auth

import android.content.Intent
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import ch.epfl.sweng.rps.R
import ch.epfl.sweng.rps.models.remote.User
import ch.epfl.sweng.rps.services.ServiceLocator
import ch.epfl.sweng.rps.utils.L
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FirebaseAuthenticator private constructor(
    private val context: ComponentActivity,
    val callback: (User) -> Unit
) :
    Authenticator() {

    private val repo = ServiceLocator.getInstance().repository
    private val resultLauncher =
        context.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { res ->
            val data: Intent? = res.data
            try {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                context.lifecycleScope.launch {
                    val account = task.await()
                    val userData = signInWithToken(account.idToken!!)
                    callback(userData)
                }
            } catch (e: Exception) {
                L.of(FirebaseAuthenticator::class.java).e("", e)
            }
        }

    private suspend fun signInWithToken(idToken: String): User {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val res = FirebaseAuth.getInstance().signInWithCredential(credential).await()
        val user = res.user!!
        return createOrGetUser(user.uid, user.displayName, user.email)
    }

    private suspend fun createOrGetUser(
        uid: String,
        displayName: String?,
        email: String?
    ): User {
        Log.d("DsName", displayName.orEmpty())
        var user = repo.getUser(uid).getOrThrow()
        if (user == null) {
            user = repo.createThisUser(displayName, email).getOrThrow()
        }
        return user
    }

    override fun signInWithGoogle() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id1))
            .requestEmail()
            .build()

        val mGoogleSignInClient = GoogleSignIn.getClient(context, gso)
        resultLauncher.launch(mGoogleSignInClient.signInIntent)
    }

    companion object {
        fun registerFor(
            context: ComponentActivity,
            callback: (User) -> Unit
        ): FirebaseAuthenticator {
            return FirebaseAuthenticator(context, callback)
        }
    }
}