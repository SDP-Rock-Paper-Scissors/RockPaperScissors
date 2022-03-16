package ch.epfl.sweng.rps.db

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.provider.Settings.Global.getString
import android.util.Log
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import ch.epfl.sweng.rps.LoginActivity
import ch.epfl.sweng.rps.MainActivity
import ch.epfl.sweng.rps.R
import ch.epfl.sweng.rps.models.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await

class FirebaseAuthenticator(val context: ComponentActivity): Authenticator{
    private var auth: FirebaseAuth = Firebase.auth
    val resultLauncher =    context.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { res ->
        val data: Intent? = res.data
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        var token: String? = ""
        try {
            val account = task.getResult(ApiException::class.java)!!

            runBlocking {
                token = signInWithToken(account.idToken!!)
                Log.d("FirebaseResult", token.orEmpty());
            }
        } catch (e: ApiException) {

        }
    }
     suspend fun signInWithToken(idToken:String) : String?{
        val credential = GoogleAuthProvider.getCredential(idToken, null)
         auth.signInWithCredential(credential).await()
         return auth.currentUser?.uid

    }
    override fun signInWithGoogle() : String{
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id1))
            .requestEmail()
            .build()

        val mGoogleSignInClient = GoogleSignIn.getClient(context, gso);
        var token:String = ""
        resultLauncher.launch(mGoogleSignInClient.signInIntent)
        return token
    }

    override fun signInAnonymously(): String {
        TODO("Not yet implemented")
    }

    companion object {
        private const val RC_SIGN_IN = 9001
        private const val TAG = "GoogleActivity"
    }

}