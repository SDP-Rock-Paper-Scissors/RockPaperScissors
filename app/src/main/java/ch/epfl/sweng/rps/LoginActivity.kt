package ch.epfl.sweng.rps

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import ch.epfl.sweng.rps.auth.FirebaseAuthenticator


const val EXTRA_MESSAGE = "ch.epfl.sweng.rps.MESSAGE"

class LoginActivity : AppCompatActivity() {

    private var callback = { user:String ->

        startActivity(Intent(this,MainActivity::class.java))
    }
    private var authenticator: FirebaseAuthenticator = FirebaseAuthenticator(this,callback)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


    }

    fun signIn(view: View) {
           authenticator.signInWithGoogle()
        }
    }




