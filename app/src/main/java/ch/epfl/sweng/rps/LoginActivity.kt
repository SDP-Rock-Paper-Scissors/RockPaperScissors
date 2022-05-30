package ch.epfl.sweng.rps

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import ch.epfl.sweng.rps.auth.FirebaseAuthenticator
import ch.epfl.sweng.rps.models.User
import ch.epfl.sweng.rps.persistence.Cache

class LoginActivity : AppCompatActivity() {

    private lateinit var cache: Cache

    private val callback = { user: User ->
        cache.setUserDetails(user)
        setLoading(false)
        launchMain()
    }

    private val authenticator: FirebaseAuthenticator =
        FirebaseAuthenticator.registerFor(this, callback)

    private fun launchMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // removes the activity from the Activity stack and prevents main from being launched twice
    }

    private fun setLoading(isLoading: Boolean) {
        findViewById<View>(R.id.loading_layout_login).visibility =
            if (isLoading) View.VISIBLE else View.GONE
        findViewById<TextView>(R.id.not_signed_in_textview).visibility =
            if (isLoading) View.GONE else View.VISIBLE
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        findViewById<Button>(R.id.signIn).setOnClickListener {
            setLoading(true)
            authenticator.signInWithGoogle()
        }
        cache = Cache.getInstance()
    }
}




