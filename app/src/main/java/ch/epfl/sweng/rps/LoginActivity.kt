package ch.epfl.sweng.rps

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import ch.epfl.sweng.rps.auth.FirebaseAuthenticator
import ch.epfl.sweng.rps.models.User
import ch.epfl.sweng.rps.storage.PrivateStorage


const val EXTRA_MESSAGE = "ch.epfl.sweng.rps.MESSAGE"

class LoginActivity : AppCompatActivity() {

    private var callback =
        {
            user:User ->
            Log.d("Here", "HERASDASDA")
            launchMain(user)
        }
    fun launchMain(user:User){
        var intent:Intent = Intent(this, MainActivity::class.java)
        val b:Bundle = Bundle()
        store.writeBackUser(user)
        b.putString("email", user.email)
        b.putString("display_name", user.username)
        b.putString("uid",user.uid)
        b.putString("privacy", user.games_history_privacy.toString())
        intent.putExtra("User",b)
        startActivity(intent)
    }

    private var authenticator: FirebaseAuthenticator = FirebaseAuthenticator(this, callback)
    private lateinit var store:PrivateStorage
    private var user:User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        store = PrivateStorage(this)
        user = store.getUserDetails()
        if (user != null)
           launchMain(user!!)
    }

    fun signIn(view: View) {
           authenticator.signInWithGoogle()
        }
    }




