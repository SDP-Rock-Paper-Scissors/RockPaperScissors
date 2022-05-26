package ch.epfl.sweng.rps

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import ch.epfl.sweng.rps.ui.onboarding.OnBoardingActivity
import ch.epfl.sweng.rps.utils.FirebaseEmulatorsUtils
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

class LoadingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)
    }


    /**
     * Here logic to setup the app
     */
    suspend fun logic() {
        Log.w("LoadingPage", "logic")

        Firebase.initialize(this@LoadingActivity)
        useEmulatorsIfNeeded()
        delay(1000)
        if (!isTest) {
            openLogin()
            finish()
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
            if (isTest) {
                throw IllegalStateException("Emulators should not be used in tests")
            }
            FirebaseEmulatorsUtils.useEmulators()
            Log.w("MainActivity", "Using emulators")
        }
    }

    val isTest: Boolean
        get() = intent.getBooleanExtra("isTest", false)

    private fun setupApp() {
        // All the logic here is to check if the user is logged in or not
        if (!hasRunLogic) {
            runBlocking { logic() }
            hasRunLogic = true
        }

        if (!isTest) {
            nav()
        }
    }


    fun nav() {
        Log.w("LoadingPage", "nav")
        val doneOnBoarding =
            intent.extras?.getBoolean(OnBoardingActivity.DONE_ONBOARDING_EXTRA, false) ?: false
        if (OnBoardingActivity.isFirstTime(this) && !doneOnBoarding) {
            Log.w("LoadingPage", "nav to onboarding")
            OnBoardingActivity.launch(this, OnBoardingActivity.Destination.FINISH)
        } else {
            Log.w("LoadingPage", "nav to main")
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }


    override fun onResume() {
        super.onResume()
        setupApp()
    }


    companion object {
        const val HELP_ME_NAV_EXTRA = "helpMeNav"
        const val IS_TEST_EXTRA = "isTest"

        private var hasRunLogic = false

        fun launch(context: Context, helpMeNav: Boolean, vararg extras: Pair<String, Any>) {
            val intent = Intent(context, LoadingActivity::class.java)
            intent.putExtra(HELP_ME_NAV_EXTRA, helpMeNav)
            extras.forEach { intent.putExtra(it.first, it.second) }
            context.startActivity(intent)
        }

        fun Intent.putExtra(key: String, value: Any) {
            when (value) {
                is String -> putExtra(key, value)
                is Int -> putExtra(key, value)
                is Boolean -> putExtra(key, value)
                is Bundle -> putExtra(key, value)
                is Array<*> -> putExtra(key, value)
                is Parcelable -> putExtra(key, value)
                is Double -> putExtra(key, value)
                is Float -> putExtra(key, value)
                is Long -> putExtra(key, value)
                else -> throw IllegalArgumentException("Type of ${value.javaClass.name} is not supported")
            }
        }
    }
}