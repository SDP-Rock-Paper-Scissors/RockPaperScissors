package ch.epfl.sweng.rps

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import ch.epfl.sweng.rps.auth.FirebaseAuthenticator
import ch.epfl.sweng.rps.models.User
import ch.epfl.sweng.rps.storage.PrivateStorage
import ch.epfl.sweng.rps.utils.useEmulators
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize


class LoginActivity : AppCompatActivity() {

    private var callback =
        { user: User ->
            Log.d("Here", "HERASDASDA")
            launchMain(user)
        }

    private fun launchMain(user: User) {
        val intent = Intent(this, MainActivity::class.java)
        val b = Bundle()
        store.writeBackUser(user)
        b.putString("email", user.email)
        b.putString("display_name", user.username)
        b.putString("uid", user.uid)
        b.putString("privacy", user.games_history_privacy.toString())
        intent.putExtra("User", b)
        startActivity(intent)
    }

    private val authenticator: FirebaseAuthenticator =
        FirebaseAuthenticator.registerFor(this, callback)

    private lateinit var store: PrivateStorage
    private var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        Firebase.initialize(this)

        useEmulatorsIfNeeded()

        store = PrivateStorage(this)
        user = store.getUserDetails()
        user?.let { Log.d("STORE", it.uid) }


        if (user != null)
            launchMain(user!!)
    }

    fun useEmulatorsIfNeeded() {
        val use = intent.getStringExtra("USE_EMULATORS")
        Log.d("MainActivity", "USE_EMULATORS: $use")
        Log.d("MainActivity", intent.extras.toString())
        if (use == "true") {
            useEmulators(context = this)
            Log.w("MainActivity", "Using emulators")
        }
    }

    fun signIn(view: View) {
        authenticator.signInWithGoogle()
    }
}




