package ch.epfl.sweng.rps

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import ch.epfl.sweng.rps.utils.FirebaseEmulatorsUtils
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

class LoadingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)
        setupApp()
    }

    private fun setupApp() {
        runBlocking {
            val isTest = intent.getBooleanExtra("isTest", false)
            Firebase.initialize(this@LoadingActivity)
            useEmulatorsIfNeeded()
            delay(1000)
            if (!isTest) {
                openLogin()
                finish()
            }
        }
    }

    private fun openLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun useEmulatorsIfNeeded() {
        val use = intent.getStringExtra("USE_EMULATORS")
        Log.d("MainActivity", "USE_EMULATORS: $use")
        if (use == "true") {
            FirebaseEmulatorsUtils.useEmulators()
            Log.w("MainActivity", "Using emulators")
        }
    }
}