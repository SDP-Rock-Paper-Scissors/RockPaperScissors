package ch.epfl.sweng.rps

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import ch.epfl.sweng.rps.auth.FirebaseAuthenticator
import ch.epfl.sweng.rps.models.User
import ch.epfl.sweng.rps.persistance.Cache
import ch.epfl.sweng.rps.persistance.PrivateStorage


class LoginActivity : AppCompatActivity() {

    private var callback =
        { user: User ->

            launchMain(user)
        }

    private fun launchMain(user: User) {
        val intent = Intent(this, MainActivity::class.java)
        val b = Bundle()
        b.putString("email", user.email)
        b.putString("display_name", user.username)
        b.putString("uid", user.uid)
        b.putString("privacy", user.games_history_privacy.toString())
        intent.putExtra("User", b)
        startActivity(intent)
        finish() // removes the activity from the Activity stack and prevents main from being launched twice
    }

    private var authenticator: FirebaseAuthenticator = FirebaseAuthenticator(this, callback)
    private val cache = Cache.getInstance(this)
    private var user: User? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        user = cache.getUserDetails()
        if (user != null){
            Log.d("CACHE", "LAUNCHING MAIN")
            launchMain(user!!)
        }
    }

    fun signIn(view: View) {
        authenticator.signInWithGoogle()
    }
}




