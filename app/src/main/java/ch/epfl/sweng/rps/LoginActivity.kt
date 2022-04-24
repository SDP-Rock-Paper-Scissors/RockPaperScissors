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
//        store.writeBackUser(user)
        b.putString("email", user.email)
        b.putString("display_name", user.username)
        b.putString("uid", user.uid)
        b.putString("privacy", user.games_history_privacy.toString())
        intent.putExtra("User", b)
        startActivity(intent)
        finish()
    }

    private var authenticator: FirebaseAuthenticator = FirebaseAuthenticator(this, callback)
    private lateinit var store: PrivateStorage
    private var user: User? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var c:Cache = Cache.getInstance(this)
        setContentView(R.layout.activity_login)
        if(user != null) return
        user = c.getUserDetails()
        if (user != null){
            Log.d("CACHE", "LAUNCHING MAIN")
            launchMain(user!!)
        }
    }

    fun signIn(view: View) {
        authenticator.signInWithGoogle()
    }
}




