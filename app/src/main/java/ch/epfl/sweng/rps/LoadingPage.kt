package ch.epfl.sweng.rps

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import ch.epfl.sweng.rps.ui.onboarding.OnBoardingActivity

class LoadingPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading_page)
        setupApp()
    }


    private fun setupApp() {
        // All the logic here is to check if the user is logged in or not
        val helpMeNav = intent.extras?.getBoolean(HELP_ME_NAV_EXTRA, false) ?: false
        if (!helpMeNav) {
            logic()
        }
        nav()
    }

    /**
     * Here logic to setup the app
     */
    fun logic() {
        Log.w("LoadingPage", "logic")
    }

    fun nav() {
        Log.w("LoadingPage", "nav")
        val doneOnBoarding =
            intent.extras?.getBoolean(OnBoardingActivity.DONE_ONBOARDING_EXTRA, false) ?: false
        if (OnBoardingActivity.isFirstTime(this) && !doneOnBoarding) {
            Log.w("LoadingPage", "nav to onboarding")
            OnBoardingActivity.launch(this, OnBoardingActivity.Destination.LOADING)
        } else {
            Log.w("LoadingPage", "nav to main")
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    companion object {
        const val HELP_ME_NAV_EXTRA = "helpMeNav"
    }
}