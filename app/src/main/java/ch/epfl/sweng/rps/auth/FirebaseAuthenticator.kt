package ch.epfl.sweng.rps.auth

import android.content.Intent
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import ch.epfl.sweng.rps.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

class FirebaseAuthenticator(val context: ComponentActivity, val callback: (String)->Unit):
    Authenticator(callback) {
    private var auth: FirebaseAuth = Firebase.auth
    private val resultLauncher =    context.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { res ->
        val data: Intent? = res.data
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(ApiException::class.java)!!
            signInWithToken(account.idToken!!)
        } catch (e: ApiException) {

        }
    }
     private fun signInWithToken(idToken:String){
         val credential = GoogleAuthProvider.getCredential(idToken, null)
         auth.signInWithCredential(credential).addOnCompleteListener { res -> callback(auth.currentUser?.uid.orEmpty()) }

    }
    override fun signInWithGoogle(){
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id1))
            .requestEmail()
            .build()

        val mGoogleSignInClient = GoogleSignIn.getClient(context, gso);
        resultLauncher.launch(mGoogleSignInClient.signInIntent)
    }

    override fun signInAnonymously() {
        TODO("Not yet implemented")
    }

    companion object {
        private const val RC_SIGN_IN = 9001
        private const val TAG = "GoogleActivity"
    }

}